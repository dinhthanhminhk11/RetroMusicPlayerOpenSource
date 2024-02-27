package code.name.monkey.retromusic.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @POST("auth/login")
    suspend fun login(@Body bodyRequest: BodyRequest): Response<LoginResponse>

    @GET("auth/getUserByToken")
    suspend fun getUserByToken(@Header("x-access-token") token: String): LoginResponse
}