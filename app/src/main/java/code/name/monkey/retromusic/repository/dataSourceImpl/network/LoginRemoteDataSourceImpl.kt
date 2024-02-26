package code.name.monkey.retromusic.repository.dataSourceImpl.network

import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.network.LoginService
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.repository.dataSource.network.LoginRemoteDataSource
import code.name.monkey.retromusic.util.logE
import retrofit2.Response

class LoginRemoteDataSourceImpl(private val loginService: LoginService) : LoginRemoteDataSource {
    override suspend fun getUser(bodyRequest: BodyRequest): Response<LoginResponse> {
        return loginService.login(bodyRequest)
    }
}