package code.name.monkey.retromusic.repository.dataSourceImpl.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.network.UserService
import code.name.monkey.retromusic.repository.dataSource.network.LoginRemoteDataSource
import retrofit2.Response

class LoginRemoteDataSourceImpl(private val userService: UserService) : LoginRemoteDataSource {
    override suspend fun getUser(bodyRequest: BodyRequest): Response<LoginResponse> {
        return userService.login(bodyRequest)
    }

    override suspend fun getUserByToken(token: String): LoginResponse =
        userService.getUserByToken(token)
}