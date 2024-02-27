package code.name.monkey.retromusic.repository.dataSource.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import retrofit2.Response

interface LoginRemoteDataSource {
    suspend fun getUser(bodyRequest: BodyRequest): Response<LoginResponse>
    suspend fun getUserByToken(token: String): LoginResponse
}