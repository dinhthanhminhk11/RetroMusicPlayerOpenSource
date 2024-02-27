package code.name.monkey.retromusic.fragments.register

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import code.name.monkey.retromusic.App
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.databinding.FragmentRegisterBinding
import code.name.monkey.retromusic.encryption.AESUtil
import code.name.monkey.retromusic.encryption.RSAUtil
import code.name.monkey.retromusic.extensions.applyToolbar
import code.name.monkey.retromusic.fragments.login.LoginViewModel
import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.user.User
import code.name.monkey.retromusic.model.user.UserClient
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.util.AppConstant
import code.name.monkey.retromusic.util.MySharedPreferences
import code.name.monkey.retromusic.util.PreferenceUtil.userName
import code.name.monkey.retromusic.util.extention.showToastError
import code.name.monkey.retromusic.util.extention.showToastSuccess
import code.name.monkey.retromusic.util.logD
import code.name.monkey.retromusic.util.logE
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: RegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel by activityViewModel<RegisterViewModel>()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerBinding = FragmentRegisterBinding.bind(view)
        _binding = RegisterBinding(registerBinding)
        applyToolbar(binding.toolbar)

        binding.email.setText("dinhthanhminhk11@gmail.com")
        binding.phone.setText("0375785587")
        binding.name.setText("Dinh MInh")
        binding.password.setText("m01675784487")

        binding.login.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.register.setOnClickListener {
            val bodyRequest = BodyRequest(
                "key",
                RSAUtil.encrypt(
                    App.getContext().getServerSecret().aesKey,
                    App.getContext().getServerSecret().publicKey
                ),
                "iv",
                RSAUtil.encrypt(
                    App.getContext().getServerSecret().aesIV,
                    App.getContext().getServerSecret().publicKey
                ),
                "body",
                AESUtil.encrypt(
                    BodyRequest(
                        "email", "${binding.email.text}",
                        "password", "${binding.password.text}",
                        "phone", "${binding.phone.text}",
                        "fullName", "${binding.name.text}",
                        "tokenDevice", "tokenDevice",
                    ).toString(),
                    App.getContext().getServerSecret().aesKey,
                    App.getContext().getServerSecret().aesIV
                )
            )

            registerViewModel.register(bodyRequest).observe(viewLifecycleOwner) { result->
                when (result) {
                    is Result.Loading -> {
                        logD("Loading")
                        isLoaded(true)
                    }

                    is Result.Error -> {
                        logE("Error")
                        isLoaded(false)
                        showToastError(activity!!, getString(R.string.notification), "Error")
                    }

                    is Result.Success -> {
                        isLoaded(false)
                        if (result.data.message.status) {
                            showToastSuccess(
                                activity!!,
                                getString(R.string.notification),
                                result.data.message.message
                            )
                            if(!result.data.data.verified){

                            }
                        } else {
                            showToastError(
                                activity!!, getString(R.string.notification),
                                result.data.message.message
                            )
                        }

                    }
                }
            }

        }
    }

    private fun isLoaded(checkLoad: Boolean) {
        if (checkLoad) {
            binding.login.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.login.isEnabled = true
            binding.progressBar.visibility = View.GONE
        }
    }
}