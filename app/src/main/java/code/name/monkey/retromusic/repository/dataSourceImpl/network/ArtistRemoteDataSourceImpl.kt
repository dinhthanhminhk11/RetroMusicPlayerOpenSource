package code.name.monkey.retromusic.repository.dataSourceImpl.network

import code.name.monkey.retromusic.model.Artist
import code.name.monkey.retromusic.network.ArtistService
import code.name.monkey.retromusic.repository.dataSource.network.ArtistRemoteDataSource
import retrofit2.Response

class ArtistRemoteDataSourceImpl(private val artistService: ArtistService) :
    ArtistRemoteDataSource {
    override suspend fun getArtistById(id: String): Response<Artist> =
        artistService.getArtistById(id)

}