package code.name.monkey.retromusic.repository.dataSource.network

import code.name.monkey.retromusic.model.Song
import retrofit2.Response

interface SongRemoteDataSource {
    suspend fun getAllSong(): Response<List<Song>>
}