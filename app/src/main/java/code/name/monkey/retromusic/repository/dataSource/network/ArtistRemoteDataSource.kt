package code.name.monkey.retromusic.repository.dataSource.network

import code.name.monkey.retromusic.model.Artist
import retrofit2.Response

interface ArtistRemoteDataSource {
    suspend fun getArtistById(id: String): Response<Artist>
}