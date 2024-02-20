package code.name.monkey.retromusic.repository.dataSource.local

import android.database.Cursor
import code.name.monkey.retromusic.model.Playlist
import code.name.monkey.retromusic.model.Song

interface PlaylistLocalDataRepository {
    fun playlist(cursor: Cursor?): Playlist

    fun searchPlaylist(query: String): List<Playlist>

    fun playlist(playlistName: String): Playlist

    fun playlists(): List<Playlist>

    fun playlists(cursor: Cursor?): List<Playlist>

    fun favoritePlaylist(playlistName: String): List<Playlist>

    fun deletePlaylist(playlistId: Long)

    fun playlist(playlistId: Long): Playlist

    fun playlistSongs(playlistId: Long): List<Song>
}