package code.name.monkey.retromusic.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.repository.RealRepositoryImpl
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val realRepositoryImpl: RealRepositoryImpl) : ViewModel() {
    fun login(bodyRequest: BodyRequest): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val loginResponse = realRepositoryImpl.getUser(bodyRequest)
                emit(loginResponse)
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
}