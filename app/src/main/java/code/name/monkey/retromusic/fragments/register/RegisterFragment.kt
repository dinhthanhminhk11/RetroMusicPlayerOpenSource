package code.name.monkey.retromusic.fragments.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.databinding.FragmentRegisterBinding
import code.name.monkey.retromusic.extensions.applyToolbar
import code.name.monkey.retromusic.fragments.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: RegisterBinding? = null
    private val binding get() = _binding!!
//    private val registerViewModel by activityViewModel<RegisterViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerBinding = FragmentRegisterBinding.bind(view)
        _binding = RegisterBinding(registerBinding)
        applyToolbar(binding.toolbar)

        binding.login.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}