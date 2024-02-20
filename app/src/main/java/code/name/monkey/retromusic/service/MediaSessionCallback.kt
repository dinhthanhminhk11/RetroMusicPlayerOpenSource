
package code.name.monkey.retromusic.service

import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import code.name.monkey.retromusic.auto.AutoMediaIDHelper
import code.name.monkey.retromusic.helper.MusicPlayerRemote
import code.name.monkey.retromusic.helper.MusicPlayerRemote.cycleRepeatMode
import code.name.monkey.retromusic.helper.ShuffleHelper.makeShuffleList
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Playlist
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.repository.*
import code.name.monkey.retromusic.repository.dataSource.local.AlbumLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.ArtistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.GenreLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.PlaylistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.SongLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.TopPlayedLocalDataRepository
import code.name.monkey.retromusic.service.MusicService.Companion.CYCLE_REPEAT
import code.name.monkey.retromusic.service.MusicService.Companion.TOGGLE_FAVORITE
import code.name.monkey.retromusic.service.MusicService.Companion.TOGGLE_SHUFFLE
import code.name.monkey.retromusic.util.MusicUtil
import code.name.monkey.retromusic.util.logD
import code.name.monkey.retromusic.util.logE
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject



class MediaSessionCallback(
    private val musicService: MusicService,
) : MediaSessionCompat.Callback(), KoinComponent {

    private val songLocalRepository by inject<SongLocalDataRepository>()
    private val albumLocalDataRepository by inject<AlbumLocalDataRepository>()
    private val artistLocalDataRepository by inject<ArtistLocalDataRepository>()
    private val genreLocalDataRepository by inject<GenreLocalDataRepository>()
    private val playlistLocalDataRepository by inject<PlaylistLocalDataRepository>()
    private val topPlayedLocalDataRepository by inject<TopPlayedLocalDataRepository>()

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        val musicId = AutoMediaIDHelper.extractMusicID(mediaId!!)
        logD("Music Id $musicId")
        val itemId = musicId?.toLong() ?: -1
        val songs: ArrayList<Song> = ArrayList()
        when (val category = AutoMediaIDHelper.extractCategory(mediaId)) {
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM -> {
                val album: Album = albumLocalDataRepository.album(itemId)
                songs.addAll(album.songs)
                musicService.openQueue(songs, 0, true)
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST -> {
                val artist: Artist = artistLocalDataRepository.artist(itemId)
                songs.addAll(artist.songs)
                musicService.openQueue(songs, 0, true)
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM_ARTIST -> {
                val artist: Artist =
                    artistLocalDataRepository.albumArtist(albumLocalDataRepository.album(itemId).albumArtist!!)
                songs.addAll(artist.songs)
                musicService.openQueue(songs, 0, true)
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_PLAYLIST -> {
                val playlist: Playlist = playlistLocalDataRepository.playlist(itemId)
                songs.addAll(playlist.getSongs())
                musicService.openQueue(songs, 0, true)
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE -> {
                songs.addAll(genreLocalDataRepository.songs(itemId))
                musicService.openQueue(songs, 0, true)
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_SHUFFLE -> {
                val allSongs: ArrayList<Song> = songLocalRepository.songs() as ArrayList<Song>
                makeShuffleList(allSongs, -1)
                musicService.openQueue(allSongs, 0, true)
            }
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_HISTORY,
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_SUGGESTIONS,
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_TOP_TRACKS,
            AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_QUEUE,
            -> {
                val tracks: List<Song> = when (category) {
                    AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_HISTORY -> topPlayedLocalDataRepository.recentlyPlayedTracks()
                    AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_SUGGESTIONS -> topPlayedLocalDataRepository.recentlyPlayedTracks()
                    AutoMediaIDHelper.MEDIA_ID_MUSICS_BY_TOP_TRACKS -> topPlayedLocalDataRepository.recentlyPlayedTracks()
                    else -> musicService.playingQueue
                }
                songs.addAll(tracks)
                var songIndex = MusicUtil.indexOfSongInList(tracks, itemId)
                if (songIndex == -1) {
                    songIndex = 0
                }
                musicService.openQueue(songs, songIndex, true)
            }
        }
        musicService.play()
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        val songs = ArrayList<Song>()
        if (query.isNullOrEmpty()) {
            // The user provided generic string e.g. 'Play music'
            // Build appropriate playlist queue
            songs.addAll(songLocalRepository.songs())
        } else {
            // Build a queue based on songs that match "query" or "extras" param
            val mediaFocus: String? = extras?.getString(MediaStore.EXTRA_MEDIA_FOCUS)
            if (mediaFocus == MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE) {
                val artistQuery = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                if (artistQuery != null) {
                    artistLocalDataRepository.artists(artistQuery).forEach {
                        songs.addAll(it.songs)
                    }
                }
            } else if (mediaFocus == MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE) {
                val albumQuery = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)
                if (albumQuery != null) {
                    albumLocalDataRepository.albums(albumQuery).forEach {
                        songs.addAll(it.songs)
                    }
                }
            }
        }

        if (songs.isEmpty()) {
            // No focus found, search by query for song title
            query?.also {
                songs.addAll(songLocalRepository.songs(it))
            }
        }

        musicService.openQueue(songs, 0, true)

        musicService.play()
    }

    override fun onPrepare() {
        super.onPrepare()
        if (musicService.currentSong != Song.emptySong)
            musicService.restoreState(::onPlay)
    }

    override fun onPlay() {
        super.onPlay()
        if (musicService.currentSong != Song.emptySong) musicService.play()
    }

    override fun onPause() {
        super.onPause()
        musicService.pause()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        musicService.playNextSong(true)
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        musicService.playPreviousSong(true)
    }

    override fun onStop() {
        super.onStop()
        musicService.quit()
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        musicService.seek(pos.toInt())
    }

    override fun onCustomAction(action: String, extras: Bundle?) {
        when (action) {
            CYCLE_REPEAT -> {
                cycleRepeatMode()
                musicService.updateMediaSessionPlaybackState()
            }

            TOGGLE_SHUFFLE -> {
                musicService.toggleShuffle()
                musicService.updateMediaSessionPlaybackState()
            }
            TOGGLE_FAVORITE -> {
                musicService.toggleFavorite()
            }
            else -> {
                logE("Unsupported action: $action")
            }
        }
    }

    private fun checkAndStartPlaying(songs: ArrayList<Song>, itemId: Long) {
        var songIndex = MusicUtil.indexOfSongInList(songs, itemId)
        if (songIndex == -1) {
            songIndex = 0
        }
        openQueue(songs, songIndex)
    }

    private fun openQueue(songs: ArrayList<Song>, index: Int, startPlaying: Boolean = true) {
        MusicPlayerRemote.openQueue(songs, index, startPlaying)
    }
}