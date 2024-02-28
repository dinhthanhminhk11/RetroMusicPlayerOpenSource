package code.name.monkey.retromusic.fragments.login

import code.name.monkey.retromusic.databinding.FragmentLoginBinding
import com.google.android.material.appbar.MaterialToolbar

class LoginBinding(loginBinding: FragmentLoginBinding) {
    val root = loginBinding.root
    val userName = loginBinding.username
    val password = loginBinding.password
    val userNameContainer = loginBinding.userNameContainer
    val passwordContainer = loginBinding.passwordContainer
    val register = loginBinding.tvSignUp
    val toolbar: MaterialToolbar = loginBinding.toolbar
    val login = loginBinding.login;
    val progressBar = loginBinding.progressBar;
}