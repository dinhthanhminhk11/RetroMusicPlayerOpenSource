package code.name.monkey.retromusic.network

import code.name.monkey.retromusic.model.Song
import retrofit2.Response
import retrofit2.http.GET

interface SongService {
    @GET("song")
    suspend fun getAllSong(): Response<List<Song>>
}