package code.name.monkey.retromusic.repository.dataSourceImpl.network

import code.name.monkey.retromusic.model.Album
import code.name.monkey.retromusic.network.AlbumService
import code.name.monkey.retromusic.repository.dataSource.network.AlbumRemoteDataSource
import retrofit2.Response

class AlbumRemoteDataSourceImpl(private val albumService: AlbumService) : AlbumRemoteDataSource {
    override suspend fun getAllAlbum(): Response<List<Album>> = albumService.getAllAlbum()
    override suspend fun getAlbumById(id: Long): Response<Album> = albumService.getAlbumById(id)
}