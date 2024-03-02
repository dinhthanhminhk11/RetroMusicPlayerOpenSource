package code.name.monkey.retromusic.repository.dataSourceImpl.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.model.response.Message
import code.name.monkey.retromusic.network.UserService
import code.name.monkey.retromusic.repository.dataSource.network.LoginRemoteDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class LoginRemoteDataSourceImpl(private val userService: UserService) : LoginRemoteDataSource {
    override suspend fun getUser(bodyRequest: BodyRequest): Response<LoginResponse> {
        return userService.login(bodyRequest)
    }

    override suspend fun register(bodyRequest: BodyRequest): Response<LoginResponse> =
        userService.register(bodyRequest)

    override suspend fun getUserByToken(token: String): LoginResponse =
        userService.getUserByToken(token)

    override suspend fun generateOTP(bodyRequest: BodyRequest): Response<Message> =
        userService.generateOTP(bodyRequest)

    override suspend fun verifyOTP(bodyRequest: BodyRequest): Response<LoginResponse> =
        userService.verifyOTP(bodyRequest)

    override suspend fun updateUser(
        email: RequestBody,
        fullName: RequestBody?,
        image: MultipartBody.Part?,
        imageBanner: MultipartBody.Part?
    ): Response<LoginResponse> {
        return userService.updateUser(email ,fullName, image, imageBanner)
    }
}