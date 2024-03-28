package code.name.monkey.retromusic.network

import code.name.monkey.retromusic.model.Album
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumService {
    @GET("album")
    suspend fun getAllAlbum(): Response<List<Album>>

    @GET("album/{id}")
    suspend fun getAlbumById(@Path("id") id: Long): Response<Album>
}