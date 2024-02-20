package code.name.monkey.retromusic.repository.dataSource.local

import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.model.Song

interface TopPlayedLocalDataRepository {
    fun recentlyPlayedTracks(): List<Song>

    fun topTracks(): List<Song>

    fun notRecentlyPlayedTracks(): List<Song>

    fun topAlbums(): List<Album>

    fun topArtists(): List<Artist>
}