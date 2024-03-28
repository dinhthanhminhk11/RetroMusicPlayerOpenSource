package code.name.monkey.retromusic.repository.dataSource.network

import code.name.monkey.retromusic.model.Album
import retrofit2.Response

interface AlbumRemoteDataSource {
    suspend fun getAllAlbum(): Response<List<Album>>
    suspend fun getAlbumById(id: Long): Response<Album>
}