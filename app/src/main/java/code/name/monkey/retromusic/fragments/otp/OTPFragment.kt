package code.name.monkey.retromusic.fragments.otp

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.TYPE_FRAGMENT_LOGIN
import code.name.monkey.retromusic.TYPE_FRAGMENT_REGISTER
import code.name.monkey.retromusic.databinding.FragmentOTPBinding
import code.name.monkey.retromusic.extensions.applyToolbar
import code.name.monkey.retromusic.extensions.findNavController
import code.name.monkey.retromusic.model.request.BodyRequest
import code.name.monkey.retromusic.model.response.UserData
import code.name.monkey.retromusic.model.user.User
import code.name.monkey.retromusic.model.user.UserClient
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.util.AppConstant
import code.name.monkey.retromusic.util.MySharedPreferences
import code.name.monkey.retromusic.util.PreferenceUtil.userName
import code.name.monkey.retromusic.util.extention.showToastError
import code.name.monkey.retromusic.util.extention.showToastSuccess
import code.name.monkey.retromusic.views.dialog.DialogConfirmCustom
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class OTPFragment : Fragment(R.layout.fragment_o_t_p) {
    private var _binding: OtpBinding? = null
    private val binding get() = _binding!!
    private val otpValidityDurationInMillis: Long = 60000
    private lateinit var countdownTimer: CountDownTimer
    private val arguments by navArgs<OTPFragmentArgs>()
    private val otpViewModel by activityViewModel<OtpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = OtpBinding(FragmentOTPBinding.bind(view))
        applyToolbar(binding.toolbar)
        initView()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView() {
        binding.toMail.text = getString(R.string.toPhone, arguments.extraEmail)

        binding.downTime.paintFlags = binding.downTime.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        startCountdownTimer()

        binding.downTime.setOnClickListener {
            countdownTimer.cancel()
            startCountdownTimer()
            otpViewModel.generateOTP(BodyRequest("email", arguments.extraEmail))
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is Result.Loading -> {
//                        isLoaded(true)
                        }

                        is Result.Error -> {
//                        isLoaded(false)
                            showToastError(activity!!, getString(R.string.notification), "Error")
                        }

                        is Result.Success -> {
                            if (it.data.status) {
                                showToastSuccess(
                                    activity!!,
                                    getString(R.string.notification),
                                    it.data.message
                                )
                            } else {
                                showToastError(
                                    activity!!,
                                    getString(R.string.notification),
                                    it.data.message
                                )
                            }
                        }
                    }
                }
        }

        binding.confirm.setOnClickListener {
            otpViewModel.verifyOTP(
                BodyRequest(
                    "email", arguments.extraEmail,
                    "OTP", binding.textOtp.text.toString()
                )
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> {
                        isLoaded(true)
                    }

                    is Result.Error -> {
                        isLoaded(false)
                        showToastError(activity!!, getString(R.string.notification), "Error")
                    }

                    is Result.Success -> {
                        isLoaded(false)
                        if (it.data.message.status) {
                            if (arguments.typeFragment == TYPE_FRAGMENT_LOGIN) {
                                loginByToken(it.data.data)
                            } else if (arguments.typeFragment == TYPE_FRAGMENT_REGISTER) {
                                DialogConfirmCustom.create(
                                    context = activity!!,
                                    content = getString(R.string.confirmLoginByOTP),
                                    onAccessClick = {
                                        loginByToken(it.data.data)
                                    },
                                    onCancelClick = {
                                        findNavController().popBackStack()
                                    }).show()
                            }
                        } else {
                            binding.textOtp.setLineColor(resources.getColor(code.name.monkey.appthemehelper.R.color.md_red_A400))
                            binding.textOtp.setTextColor(resources.getColor(code.name.monkey.appthemehelper.R.color.md_red_A400))
                            showToastError(
                                activity!!,
                                getString(R.string.notification),
                                it.data.message.message
                            )
                        }
                    }
                }
            }
//            binding.textOtp.setLineColor(resources.getColor(code.name.monkey.appthemehelper.R.color.md_red_A400))
//            binding.textOtp.setTextColor(resources.getColor(code.name.monkey.appthemehelper.R.color.md_red_A400))
        }
    }

    private fun loginByToken(data: UserData) {
        MySharedPreferences.getInstance(requireActivity())
            .putString(AppConstant.TOKEN_USER, data.accessToken)
        otpViewModel.loginByToken(data.accessToken)
            .observe(viewLifecycleOwner) { result ->
                UserClient.setUserFromUser(
                    User(
                        _id = result.data._id,
                        fullName = result.data.fullName,
                        email = result.data.email,
                        phone = result.data.phone,
                        image = result.data.image
                    )
                )
                userName = result.data.fullName
                findNavController().popBackStack(R.id.user_info_fragment, false)
            }
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(otpValidityDurationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val downTime = String.format("%d:%02d", minutes, seconds)
                binding.downTime.setTextColor(resources.getColor(code.name.monkey.appthemehelper.R.color.md_red_A400))
                binding.downTime.text = downTime
                binding.downTime.isEnabled = false
            }

            override fun onFinish() {
                binding.downTime.text = getString(R.string.sendAgain)
                binding.downTime.isEnabled = true
                binding.downTime.setTextColor(resources.getColor(R.color.black_color))
            }
        }
        countdownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }

    private fun isLoaded(checkLoad: Boolean) {
        if (checkLoad) {
            binding.confirm.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.confirm.isEnabled = true
            binding.progressBar.visibility = View.GONE
        }
    }
}