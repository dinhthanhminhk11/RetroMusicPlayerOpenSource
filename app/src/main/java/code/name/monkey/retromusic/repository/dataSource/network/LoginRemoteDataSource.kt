package code.name.monkey.retromusic.repository.dataSource.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.network.Result
import retrofit2.Response

interface LoginRemoteDataSource {
    suspend fun getUser(bodyRequest: BodyRequest): Response<LoginResponse>
}