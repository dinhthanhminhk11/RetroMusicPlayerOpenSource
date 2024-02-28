package code.name.monkey.retromusic.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val data: UserData,
    @SerializedName("message") val message: Message
)