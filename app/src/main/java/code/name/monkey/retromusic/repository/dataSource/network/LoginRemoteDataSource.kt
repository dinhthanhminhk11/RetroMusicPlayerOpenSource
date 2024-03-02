package code.name.monkey.retromusic.repository.dataSource.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.model.response.Message
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface LoginRemoteDataSource {
    suspend fun getUser(bodyRequest: BodyRequest): Response<LoginResponse>
    suspend fun register(bodyRequest: BodyRequest): Response<LoginResponse>
    suspend fun getUserByToken(token: String): LoginResponse
    suspend fun generateOTP(bodyRequest: BodyRequest): Response<Message>
    suspend fun verifyOTP(bodyRequest: BodyRequest): Response<LoginResponse>
    suspend fun updateUser(
        email: RequestBody,
        fullName: RequestBody?,
        image: MultipartBody.Part?,
        imageBanner: MultipartBody.Part?,
    ): Response<LoginResponse>
}