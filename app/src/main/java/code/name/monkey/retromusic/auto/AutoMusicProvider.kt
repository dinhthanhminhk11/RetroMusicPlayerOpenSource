
package code.name.monkey.retromusic.auto

import android.content.Context
import android.content.res.Resources
import android.support.v4.media.MediaBrowserCompat
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.helper.MusicPlayerRemote
import code.name.monkey.retromusic.model.CategoryInfo
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.repository.*
import code.name.monkey.retromusic.repository.dataSource.local.AlbumLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.ArtistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.GenreLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.PlaylistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.SongLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.TopPlayedLocalDataRepository
import code.name.monkey.retromusic.service.MusicService
import code.name.monkey.retromusic.util.MusicUtil
import code.name.monkey.retromusic.util.PreferenceUtil
import java.lang.ref.WeakReference


/**
 * Created by Beesham Sarendranauth (Beesham)
 */
class AutoMusicProvider(
    private val mContext: Context,
    private val songsRepository: SongLocalDataRepository,
    private val albumsRepository: AlbumLocalDataRepository,
    private val artistsRepository: ArtistLocalDataRepository,
    private val genresRepository: GenreLocalDataRepository,
    private val playlistsRepository: PlaylistLocalDataRepository,
    private val topPlayedLocalDataRepository: TopPlayedLocalDataRepository
) {
    private var mMusicService: WeakReference<MusicService>? = null

    fun setMusicService(service: MusicService) {
        mMusicService = WeakReference(service)
    }

    fun getChildren(mediaId: String?, resources: Resources): List<MediaBrowserCompat.MediaItem> {
        val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
        when (mediaId) {
            AutoMediaIDHelper.MEDIA_ID_ROOT -> {
                mediaItems.addAll(getRootChildren(resources))
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_PLAYLIST -> for (playlist in playlistsRepository.playlists()) {
                mediaItems.add(
                    AutoMediaItem.with(mContext)
                        .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_PLAYLIST, playlist.id)
                        .icon(R.drawable.ic_playlist_play)
                        .title(playlist.name)
                        .subTitle(playlist.getInfoString(mContext))
                        .asPlayable()
                        .build()
                )
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM -> for (album in albumsRepository.albums()) {
                mediaItems.add(
                    AutoMediaItem.with(mContext)
                        .path(mediaId, album.id)
                        .title(album.title)
                        .subTitle(album.albumArtist ?: album.artistName)
                        .icon(MusicUtil.getMediaStoreAlbumCoverUri(album.id))
                        .asPlayable()
                        .build()
                )
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST -> for (artist in artistsRepository.artists()) {
                mediaItems.add(
                    AutoMediaItem.with(mContext)
                        .asPlayable()
                        .path(mediaId, artist.id)
                        .title(artist.name)
                        .build()
                )
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM_ARTIST -> for (artist in artistsRepository.albumArtists()) {
                mediaItems.add(
                    AutoMediaItem.with(mContext)
                        .asPlayable()
                        // we just pass album id here as we don't have album artist id's
                        .path(mediaId, artist.safeGetFirstAlbum().id)
                        .title(artist.name)
                        .build()
                )
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE -> for (genre in genresRepository.genres()) {
                mediaItems.add(
                    AutoMediaItem.with(mContext)
                        .asPlayable()
                        .path(mediaId, genre.id)
                        .title(genre.name)
                        .build()
                )
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_QUEUE ->
                mMusicService?.get()?.playingQueue
                    ?.let {
                        for (song in it) {
                            mediaItems.add(
                                AutoMediaItem.with(mContext)
                                    .asPlayable()
                                    .path(mediaId, song.id)
                                    .title(song.title)
                                    .subTitle(song.artistName)
                                    .icon(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId))
                                    .build()
                            )
                        }
                    }
            else -> {
                getPlaylistChildren(mediaId, mediaItems)
            }
        }
        return mediaItems
    }

    private fun getPlaylistChildren(
        mediaId: String?,
        mediaItems: MutableList<MediaBrowserCompat.MediaItem>
    ) {
        val songs = when (mediaId) {
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_TOP_TRACKS -> {
                topPlayedLocalDataRepository.topTracks()
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_HISTORY -> {
                topPlayedLocalDataRepository.recentlyPlayedTracks()
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_SUGGESTIONS -> {
                topPlayedLocalDataRepository.notRecentlyPlayedTracks().take(8)
            }
            else -> {
                emptyList()
            }
        }
        songs.forEach { song ->
            mediaItems.add(
                getPlayableSong(mediaId, song)
            )
        }
    }

    private fun getRootChildren(resources: Resources): List<MediaBrowserCompat.MediaItem> {
        val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
        val libraryCategories = PreferenceUtil.libraryCategory
        libraryCategories.forEach {
            if (it.visible) {
                when (it.category) {
                    CategoryInfo.Category.Albums -> {
                        mediaItems.add(
                            AutoMediaItem.with(mContext)
                                .asBrowsable()
                                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM)
                                .gridLayout(true)
                                .icon(R.drawable.ic_album)
                                .title(resources.getString(R.string.albums)).build()
                        )
                    }
                    CategoryInfo.Category.Artists -> {
                        if (PreferenceUtil.albumArtistsOnly) {
                            mediaItems.add(
                                AutoMediaItem.with(mContext)
                                    .asBrowsable()
                                    .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM_ARTIST)
                                    .icon(R.drawable.ic_album_artist)
                                    .title(resources.getString(R.string.album_artist)).build()
                            )
                        } else {
                            mediaItems.add(
                                AutoMediaItem.with(mContext)
                                    .asBrowsable()
                                    .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST)
                                    .icon(R.drawable.ic_artist)
                                    .title(resources.getString(R.string.artists)).build()
                            )
                        }
                    }
                    CategoryInfo.Category.Genres -> {
                        mediaItems.add(
                            AutoMediaItem.with(mContext)
                                .asBrowsable()
                                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE)
                                .icon(R.drawable.ic_guitar)
                                .title(resources.getString(R.string.genres)).build()
                        )
                    }
                    CategoryInfo.Category.Playlists -> {
                        mediaItems.add(
                            AutoMediaItem.with(mContext)
                                .asBrowsable()
                                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_PLAYLIST)
                                .icon(R.drawable.ic_playlist_play)
                                .title(resources.getString(R.string.playlists)).build()
                        )
                    }
                    else -> {
                    }
                }
            }
        }
        mediaItems.add(
            AutoMediaItem.with(mContext)
                .asPlayable()
                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_SHUFFLE)
                .icon(R.drawable.ic_shuffle)
                .title(resources.getString(R.string.action_shuffle_all))
                .subTitle(MusicUtil.getPlaylistInfoString(mContext, songsRepository.songs()))
                .build()
        )
        mediaItems.add(
            AutoMediaItem.with(mContext)
                .asBrowsable()
                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_QUEUE)
                .icon(R.drawable.ic_queue_music)
                .title(resources.getString(R.string.queue))
                .subTitle(MusicUtil.getPlaylistInfoString(mContext, MusicPlayerRemote.playingQueue))
                .asBrowsable().build()
        )
        mediaItems.add(
            AutoMediaItem.with(mContext)
                .asBrowsable()
                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_TOP_TRACKS)
                .icon(R.drawable.ic_trending_up)
                .title(resources.getString(R.string.my_top_tracks))
                .subTitle(
                    MusicUtil.getPlaylistInfoString(
                        mContext,
                        topPlayedLocalDataRepository.topTracks()
                    )
                )
                .asBrowsable().build()
        )
        mediaItems.add(
            AutoMediaItem.with(mContext)
                .asBrowsable()
                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_SUGGESTIONS)
                .icon(R.drawable.ic_face)
                .title(resources.getString(R.string.suggestion_songs))
                .subTitle(
                    MusicUtil.getPlaylistInfoString(
                        mContext,
                        topPlayedLocalDataRepository.notRecentlyPlayedTracks().takeIf {
                            it.size > 9
                        } ?: emptyList()
                    )
                )
                .asBrowsable().build()
        )
        mediaItems.add(
            AutoMediaItem.with(mContext)
                .asBrowsable()
                .path(AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_HISTORY)
                .icon(R.drawable.ic_history)
                .title(resources.getString(R.string.history))
                .subTitle(
                    MusicUtil.getPlaylistInfoString(
                        mContext,
                        topPlayedLocalDataRepository.recentlyPlayedTracks()
                    )
                )
                .asBrowsable().build()
        )
        return mediaItems
    }

    private fun getPlayableSong(mediaId: String?, song: Song): MediaBrowserCompat.MediaItem {
        return AutoMediaItem.with(mContext)
            .asPlayable()
            .path(mediaId, song.id)
            .title(song.title)
            .subTitle(song.artistName)
            .icon(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId))
            .build()
    }
}