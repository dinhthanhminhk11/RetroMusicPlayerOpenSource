package code.name.monkey.retromusic.fragments.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.App
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.appshortcuts.DynamicShortcutManager
import code.name.monkey.retromusic.databinding.FragmentLoginBinding
import code.name.monkey.retromusic.encryption.AESUtil
import code.name.monkey.retromusic.encryption.RSAUtil
import code.name.monkey.retromusic.extensions.applyToolbar
import code.name.monkey.retromusic.extensions.findNavControllerOpen
import code.name.monkey.retromusic.extensions.isValidEmail
import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.user.User
import code.name.monkey.retromusic.model.user.UserClient
import code.name.monkey.retromusic.model.user.UserClient.phone
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.util.AppConstant
import code.name.monkey.retromusic.util.MySharedPreferences
import code.name.monkey.retromusic.util.PreferenceUtil.userName
import code.name.monkey.retromusic.util.extention.showToastError
import code.name.monkey.retromusic.util.extention.showToastSuccess
import code.name.monkey.retromusic.util.logD
import code.name.monkey.retromusic.util.logE
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class LoginFragment : Fragment(R.layout.fragment_login), ColorCallback {
    private var _binding: LoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel by activityViewModel<LoginViewModel>()

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginBinding = FragmentLoginBinding.bind(view)
        _binding = LoginBinding(loginBinding)
        applyToolbar(binding.toolbar)

        binding.password.setText("m01675784487")
        binding.userName.setText("minhk11642002@gmail.com")
        binding.login.setOnClickListener {
            if (binding.userName.text!!.isEmpty()) {
                showToastError(
                    activity!!, getString(R.string.notification),
                    getString(R.string.enterMail)
                )
            } else if (!isValidEmail(binding.userName.text.toString())) {
                showToastError(
                    activity!!, getString(R.string.notification),
                    getString(R.string.enterMailFaild)
                )
            } else if (binding.password.text!!.isEmpty()) {
                showToastError(
                    activity!!, getString(R.string.notification),
                    getString(R.string.enterPass)
                )
            } else {
                loginViewModel.login(
                    BodyRequest(
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
                                "username", "${binding.userName.text}",
                                "password", "${binding.password.text}"
                            ).toString(),
                            App.getContext().getServerSecret().aesKey,
                            App.getContext().getServerSecret().aesIV
                        )
                    )
                ).observe(viewLifecycleOwner) { result ->
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
                                userName = result.data.data.fullName
                                UserClient.setUserFromUser(
                                    User(
                                        _id = result.data.data._id,
                                        fullName = result.data.data.fullName,
                                        email = result.data.data.email,
                                        phone = result.data.data.phone,
                                        image = result.data.data.image
                                    )
                                )
                                MySharedPreferences.getInstance(requireActivity())
                                    .putString(AppConstant.TOKEN_USER, result.data.data.accessToken)
                                findNavController().popBackStack()
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
        binding.register.setOnClickListener {
            findNavController().findNavControllerOpen(R.id.registerFragment)
        }
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        ThemeStore.editTheme(requireContext()).accentColor(color).commit()
        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(requireContext()).updateDynamicShortcuts()
        activity?.recreate()
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