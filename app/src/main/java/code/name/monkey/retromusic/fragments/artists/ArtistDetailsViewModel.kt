package code.name.monkey.retromusic.fragments.artists

import androidx.lifecycle.*
import code.name.monkey.retromusic.interfaces.IMusicServiceEventListener
import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.network.model.LastFmArtist
import code.name.monkey.retromusic.repository.RealRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ArtistDetailsViewModel(
    private val realRepositoryImpl: RealRepositoryImpl,
    private val artistId: String?
) : ViewModel(), IMusicServiceEventListener {
    private val artistDetails = MutableLiveData<Result<Artist>>()

    init {
        fetchArtist()
    }

    private fun fetchArtist() {
        viewModelScope.launch(IO) {
            artistId?.let { artistDetails.postValue(realRepositoryImpl.getArtistById(it)) }
//            artistName?.let { /*artistDetails.postValue(realRepositoryImpl.albumArtistByName(it))*/ }
        }
    }

    fun getArtist(): LiveData<Result<Artist>> = artistDetails

    fun getArtistInfo(
        name: String,
        lang: String?,
        cache: String?
    ): LiveData<Result<LastFmArtist>> = liveData(IO) {
        emit(Result.Loading)
        val info = realRepositoryImpl.artistInfo(name, lang, cache)
        emit(info)
    }

    override fun onMediaStoreChanged() {
        fetchArtist()
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
