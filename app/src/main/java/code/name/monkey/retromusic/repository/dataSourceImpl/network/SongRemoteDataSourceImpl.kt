package code.name.monkey.retromusic.repository.dataSourceImpl.network

import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.network.SongService
import code.name.monkey.retromusic.repository.dataSource.network.SongRemoteDataSource
import retrofit2.Response

class SongRemoteDataSourceImpl(private val songService: SongService) : SongRemoteDataSource {
    override suspend fun getAllSong(): Response<List<Song>> = songService.getAllSong()
}