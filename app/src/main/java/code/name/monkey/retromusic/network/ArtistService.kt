package code.name.monkey.retromusic.network

import code.name.monkey.retromusic.model.Artist
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistService {
    @GET("artist/{id}")
    suspend fun getArtistById(@Path("id") id: String): Response<Artist>
}