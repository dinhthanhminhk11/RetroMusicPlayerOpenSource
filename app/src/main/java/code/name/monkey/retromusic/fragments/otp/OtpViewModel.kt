package code.name.monkey.retromusic.fragments.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.LoginResponse
import code.name.monkey.retromusic.model.response.Message
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.repository.RealRepositoryImpl
import kotlinx.coroutines.Dispatchers


class OtpViewModel(private val realRepositoryImpl: RealRepositoryImpl) : ViewModel() {
    fun verifyOTP(bodyRequest: BodyRequest): LiveData<Result<LoginResponse>> {
        return liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val registerResponse = realRepositoryImpl.verifyOTP(bodyRequest)
                emit(registerResponse)
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }

    fun generateOTP(bodyRequest: BodyRequest): LiveData<Result<Message>> {
        return liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = realRepositoryImpl.generateOTP(bodyRequest)
                emit(response)
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }
}