package code.name.monkey.retromusic.model.response

data class UserData(
    val _id: String,
    val email: String,
    val phone: String,
    val tokenDevice: String,
    val accessToken: String,
    val verified: Boolean
)