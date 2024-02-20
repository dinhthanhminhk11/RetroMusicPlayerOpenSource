package code.name.monkey.retromusic.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import code.name.monkey.retromusic.FAVOURITES
import code.name.monkey.retromusic.GENRES
import code.name.monkey.retromusic.PLAYLISTS
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.RECENT_ALBUMS
import code.name.monkey.retromusic.RECENT_ARTISTS
import code.name.monkey.retromusic.TOP_ALBUMS
import code.name.monkey.retromusic.TOP_ARTISTS
import code.name.monkey.retromusic.db.HistoryEntity
import code.name.monkey.retromusic.db.PlayCountEntity
import code.name.monkey.retromusic.db.PlaylistEntity
import code.name.monkey.retromusic.db.PlaylistWithSongs
import code.name.monkey.retromusic.db.SongEntity
import code.name.monkey.retromusic.db.fromHistoryToSongs
import code.name.monkey.retromusic.db.toSong
import code.name.monkey.retromusic.fragments.search.Filter
import code.name.monkey.retromusic.model.AbsCustomPlaylist
import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Contributor
import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.model.Home
import code.name.monkey.retromusic.model.Playlist
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.model.smartplaylist.NotPlayedPlaylist
import code.name.monkey.retromusic.network.LastFMService
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.network.Result.Error
import code.name.monkey.retromusic.network.Result.Success
import code.name.monkey.retromusic.network.model.LastFmAlbum
import code.name.monkey.retromusic.network.model.LastFmArtist
import code.name.monkey.retromusic.repository.dataSource.local.AlbumLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.ArtistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.GenreLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.LastAddedLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.LocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.PlaylistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.RoomLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.SongLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.TopPlayedLocalDataRepository
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealSearchRepositoryImpl
import code.name.monkey.retromusic.util.logE

class RealRepositoryImpl(
    private val context: Context,
    private val lastFMService: LastFMService,
    private val songLocalDataRepository: SongLocalDataRepository,
    private val albumLocalDataRepository: AlbumLocalDataRepository,
    private val artistLocalDataRepository: ArtistLocalDataRepository,
    private val genreLocalDataRepository: GenreLocalDataRepository,
    private val lastAddedLocalDataRepository: LastAddedLocalDataRepository,
    private val playlistLocalDataRepository: PlaylistLocalDataRepository,
    private val searchRepository: RealSearchRepositoryImpl,
    private val topPlayedLocalDataRepository: TopPlayedLocalDataRepository,
    private val roomLocalDataRepository: RoomLocalDataRepository,
    private val localDataRepository: LocalDataRepository,
) : Repository {

    override suspend fun deleteSongs(songs: List<Song>) = roomLocalDataRepository.deleteSongs(songs)

    override suspend fun contributor(): List<Contributor> = localDataRepository.contributors()

    override suspend fun searchSongs(query: String): List<Song> = songLocalDataRepository.songs(query)

    override suspend fun searchAlbums(query: String): List<Album> = albumLocalDataRepository.albums(query)

    override suspend fun isSongFavorite(songId: Long): Boolean =
        roomLocalDataRepository.isSongFavorite(context, songId)

    override fun getSongByGenre(genreId: Long): Song = genreLocalDataRepository.song(genreId)

    override suspend fun searchArtists(query: String): List<Artist> =
        artistLocalDataRepository.artists(query)

    override suspend fun fetchAlbums(): List<Album> = albumLocalDataRepository.albums()

    override suspend fun albumByIdAsync(albumId: Long): Album = albumLocalDataRepository.album(albumId)

    override fun albumById(albumId: Long): Album = albumLocalDataRepository.album(albumId)

    override suspend fun fetchArtists(): List<Artist> = artistLocalDataRepository.artists()

    override suspend fun albumArtists(): List<Artist> = artistLocalDataRepository.albumArtists()

    override suspend fun artistById(artistId: Long): Artist = artistLocalDataRepository.artist(artistId)

    override suspend fun albumArtistByName(name: String): Artist =
        artistLocalDataRepository.albumArtist(name)

    override suspend fun recentArtists(): List<Artist> = lastAddedLocalDataRepository.recentArtists()

    override suspend fun recentAlbums(): List<Album> = lastAddedLocalDataRepository.recentAlbums()

    override suspend fun topArtists(): List<Artist> = topPlayedLocalDataRepository.topArtists()

    override suspend fun topAlbums(): List<Album> = topPlayedLocalDataRepository.topAlbums()

    override suspend fun fetchLegacyPlaylist(): List<Playlist> = playlistLocalDataRepository.playlists()

    override suspend fun fetchGenres(): List<Genre> = genreLocalDataRepository.genres()

    override suspend fun allSongs(): List<Song> = songLocalDataRepository.songs()

    override suspend fun search(query: String?, filter: Filter): MutableList<Any> =
        searchRepository.searchAll(context, query, filter)

    override suspend fun getPlaylistSongs(playlist: Playlist): List<Song> =
        if (playlist is AbsCustomPlaylist) {
            playlist.songs()
        } else {
            PlaylistSongsLoader.getPlaylistSongList(context, playlist.id)
        }

    override suspend fun getGenre(genreId: Long): List<Song> = genreLocalDataRepository.songs(genreId)

    override suspend fun artistInfo(
        name: String,
        lang: String?,
        cache: String?,
    ): Result<LastFmArtist> {
        return try {
            Success(lastFMService.artistInfo(name, lang, cache))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
    }

    override suspend fun albumInfo(
        artist: String,
        album: String,
    ): Result<LastFmAlbum> {
        return try {
            val lastFmAlbum = lastFMService.albumInfo(artist, album)
            Success(lastFmAlbum)
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
    }

    override suspend fun homeSections(): List<Home> {
        val homeSections = mutableListOf<Home>()
        val sections: List<Home> = listOf(
            topArtistsHome(),
            topAlbumsHome(),
            recentArtistsHome(),
            recentAlbumsHome(),
            favoritePlaylistHome()
        )
        for (section in sections) {
            if (section.arrayList.isNotEmpty()) {
                homeSections.add(section)
            }
        }
        return homeSections
    }


    override suspend fun playlist(playlistId: Long) =
        playlistLocalDataRepository.playlist(playlistId)

    override suspend fun fetchPlaylistWithSongs(): List<PlaylistWithSongs> =
        roomLocalDataRepository.playlistWithSongs()

    override fun getPlaylist(playlistId: Long): LiveData<PlaylistWithSongs> =
        roomLocalDataRepository.getPlaylist(playlistId)

    override suspend fun playlistSongs(playlistWithSongs: PlaylistWithSongs): List<Song> =
        playlistWithSongs.songs.map {
            it.toSong()
        }

    override fun playlistSongs(playListId: Long): LiveData<List<SongEntity>> =
        roomLocalDataRepository.getSongs(playListId)

    override suspend fun insertSongs(songs: List<SongEntity>) =
        roomLocalDataRepository.insertSongs(songs)

    override suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity> =
        roomLocalDataRepository.checkPlaylistExists(playlistName)

    override fun checkPlaylistExists(playListId: Long): LiveData<Boolean> =
        roomLocalDataRepository.checkPlaylistExists(playListId)

    override suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long =
        roomLocalDataRepository.createPlaylist(playlistEntity)

    override suspend fun fetchPlaylists(): List<PlaylistEntity> = roomLocalDataRepository.playlists()

    override suspend fun deleteRoomPlaylist(playlists: List<PlaylistEntity>) =
        roomLocalDataRepository.deletePlaylistEntities(playlists)

    override suspend fun renameRoomPlaylist(playlistId: Long, name: String) =
        roomLocalDataRepository.renamePlaylistEntity(playlistId, name)

    override suspend fun deleteSongsInPlaylist(songs: List<SongEntity>) =
        roomLocalDataRepository.deleteSongsInPlaylist(songs)

    override suspend fun removeSongFromPlaylist(songEntity: SongEntity) =
        roomLocalDataRepository.removeSongFromPlaylist(songEntity)

    override suspend fun deletePlaylistSongs(playlists: List<PlaylistEntity>) =
        roomLocalDataRepository.deletePlaylistSongs(playlists)

    override suspend fun favoritePlaylist(): PlaylistEntity =
        roomLocalDataRepository.favoritePlaylist(context.getString(R.string.favorites))

    override suspend fun isFavoriteSong(songEntity: SongEntity): List<SongEntity> =
        roomLocalDataRepository.isFavoriteSong(songEntity)

    override suspend fun addSongToHistory(currentSong: Song) =
        roomLocalDataRepository.addSongToHistory(currentSong)

    override suspend fun songPresentInHistory(currentSong: Song): HistoryEntity? =
        roomLocalDataRepository.songPresentInHistory(currentSong)

    override suspend fun updateHistorySong(currentSong: Song) =
        roomLocalDataRepository.updateHistorySong(currentSong)

    override suspend fun favoritePlaylistSongs(): List<SongEntity> =
        roomLocalDataRepository.favoritePlaylistSongs(context.getString(R.string.favorites))

    override suspend fun recentSongs(): List<Song> = lastAddedLocalDataRepository.recentSongs()

    override suspend fun topPlayedSongs(): List<Song> = topPlayedLocalDataRepository.topTracks()

    override suspend fun insertSongInPlayCount(playCountEntity: PlayCountEntity) =
        roomLocalDataRepository.insertSongInPlayCount(playCountEntity)

    override suspend fun updateSongInPlayCount(playCountEntity: PlayCountEntity) =
        roomLocalDataRepository.updateSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInPlayCount(playCountEntity: PlayCountEntity) =
        roomLocalDataRepository.deleteSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInHistory(songId: Long) =
        roomLocalDataRepository.deleteSongInHistory(songId)

    override suspend fun clearSongHistory() {
        roomLocalDataRepository.clearSongHistory()
    }

    override suspend fun checkSongExistInPlayCount(songId: Long): List<PlayCountEntity> =
        roomLocalDataRepository.checkSongExistInPlayCount(songId)

    override suspend fun playCountSongs(): List<PlayCountEntity> =
        roomLocalDataRepository.playCountSongs()

    override fun observableHistorySongs(): LiveData<List<Song>> =
        roomLocalDataRepository.observableHistorySongs().map {
            it.fromHistoryToSongs()
        }

    override fun historySong(): List<HistoryEntity> =
        roomLocalDataRepository.historySongs()

    override fun favorites(): LiveData<List<SongEntity>> =
        roomLocalDataRepository.favoritePlaylistLiveData(context.getString(R.string.favorites))

    override suspend fun suggestions(): List<Song> {
        return NotPlayedPlaylist().songs().shuffled().takeIf {
            it.size > 9
        } ?: emptyList()
    }

    override suspend fun genresHome(): Home {
        val genres = genreLocalDataRepository.genres().shuffled()
        return Home(genres, GENRES, R.string.genres)
    }

    override suspend fun playlists(): Home {
        val playlist = playlistLocalDataRepository.playlists()
        return Home(playlist, PLAYLISTS, R.string.playlists)
    }

    override suspend fun recentArtistsHome(): Home {
        val artists = lastAddedLocalDataRepository.recentArtists().take(5)
        return Home(artists, RECENT_ARTISTS, R.string.recent_artists)
    }

    override suspend fun recentAlbumsHome(): Home {
        val albums = lastAddedLocalDataRepository.recentAlbums().take(5)
        return Home(albums, RECENT_ALBUMS, R.string.recent_albums)
    }

    override suspend fun topAlbumsHome(): Home {
        val albums = topPlayedLocalDataRepository.topAlbums().take(5)
        return Home(albums, TOP_ALBUMS, R.string.top_albums)
    }

    override suspend fun topArtistsHome(): Home {
        val artists = topPlayedLocalDataRepository.topArtists().take(5) // lấy ra 5 phần tử lưu vào list
        return Home(artists, TOP_ARTISTS, R.string.top_artists)
    }

    override suspend fun favoritePlaylistHome(): Home {
        val songs = favoritePlaylistSongs().map {
            it.toSong()
        }
        return Home(songs, FAVOURITES, R.string.favorites)
    }
}