package code.name.monkey.retromusic.fragments.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.repository.RealRepositoryImpl
import kotlinx.coroutines.Dispatchers

class RegisterViewModel(private val realRepositoryImpl: RealRepositoryImpl) : ViewModel() {
    fun register(bodyRequest: BodyRequest): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val registerResponse = realRepositoryImpl.register(bodyRequest)
                emit(registerResponse)
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
}