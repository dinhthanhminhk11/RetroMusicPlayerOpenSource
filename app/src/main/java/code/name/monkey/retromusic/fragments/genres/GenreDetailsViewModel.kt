
package code.name.monkey.retromusic.fragments.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.interfaces.IMusicServiceEventListener
import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.repository.RealRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenreDetailsViewModel(
    private val realRepositoryImpl: RealRepositoryImpl,
    private val genre: Genre
) : ViewModel(), IMusicServiceEventListener {

    private val _playListSongs = MutableLiveData<List<Song>>()
    private val _genre = MutableLiveData<Genre>().apply {
        postValue(genre)
    }

    fun getSongs(): LiveData<List<Song>> = _playListSongs

    fun getGenre(): LiveData<Genre> = _genre

    init {
        loadGenreSongs(genre)
    }

    private fun loadGenreSongs(genre: Genre) = viewModelScope.launch(IO) {
        val songs = realRepositoryImpl.getGenre(genre.id)
        withContext(Main) { _playListSongs.postValue(songs) }
    }

    override fun onMediaStoreChanged() {
        loadGenreSongs(genre)
    }

    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onQueueChanged() {}
    override fun onPlayingMetaChanged() {}
    override fun onPlayStateChanged() {}
    override fun onRepeatModeChanged() {}
    override fun onShuffleModeChanged() {}
    override fun onFavoriteStateChanged() {}
}
