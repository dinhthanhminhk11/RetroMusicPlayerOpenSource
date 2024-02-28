package code.name.monkey.retromusic.fragments.otp

import code.name.monkey.retromusic.databinding.FragmentOTPBinding
import code.name.monkey.retromusic.views.pinview.OtpView

class OtpBinding(binding: FragmentOTPBinding) {
    val textOtp : OtpView = binding.otp
    val downTime = binding.downTime
    val confirm = binding.confirm
    val toolbar = binding.toolbar
    val root = binding.root
    val toMail = binding.toMail
    val progressBar = binding.progressBar
}