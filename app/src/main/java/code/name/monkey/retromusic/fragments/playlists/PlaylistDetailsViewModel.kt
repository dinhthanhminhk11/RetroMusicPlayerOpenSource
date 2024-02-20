
package code.name.monkey.retromusic.fragments.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import code.name.monkey.retromusic.db.PlaylistWithSongs
import code.name.monkey.retromusic.db.SongEntity
import code.name.monkey.retromusic.repository.RealRepositoryImpl

class PlaylistDetailsViewModel(
    private val realRepositoryImpl: RealRepositoryImpl,
    private var playlistId: Long
) : ViewModel() {
    fun getSongs(): LiveData<List<SongEntity>> =
        realRepositoryImpl.playlistSongs(playlistId)

    fun playlistExists(): LiveData<Boolean> =
        realRepositoryImpl.checkPlaylistExists(playlistId)

    fun getPlaylist(): LiveData<PlaylistWithSongs> = realRepositoryImpl.getPlaylist(playlistId)
}
