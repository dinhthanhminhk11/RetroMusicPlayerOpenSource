package code.name.monkey.retromusic.model.user


object UserClient {
    var id: String? = null
    var name: String? = null
    var email: String? = null
    var image: String? = null
    var banner: String? = null
    var phone: String? = null

    fun setUserFromUser(user: User) {
        id = user._id
        name = user.fullName
        email = user.email
        image = user.image
        banner = user.imageBanner
        phone = user.phone
    }
}