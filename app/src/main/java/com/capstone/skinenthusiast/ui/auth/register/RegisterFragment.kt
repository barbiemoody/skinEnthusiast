package com.capstone.skinenthusiast.ui.auth.register

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.FragmentRegisterBinding
import com.capstone.skinenthusiast.ui.auth.login.LoginFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.capstone.skinenthusiast.utils.Result

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val registerViewModel by viewModel<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        observeViewModel()
        setListeners()

        return binding.root
    }

    private fun observeViewModel() {
        registerViewModel.apply {
            isLoading.observe(viewLifecycleOwner, ::showLoading)
            errorMessage.observe(viewLifecycleOwner, ::showToast)
        }
    }

    private fun setListeners() {
        binding.apply {
            btnRegister.setOnClickRegisterListenerWithValidation(
                edName = binding.edName,
                edEmail = binding.edEmail,
                edPassword = binding.edPassword,
                rgGender = binding.rgGender,
            ) {
                switchToLogin()
            }

            btnLogin.setOnClickListener {
                switchToLogin()
            }
        }
    }

    private fun switchToLogin() {
        with(parentFragmentManager.beginTransaction()) {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            replace(
                R.id.auth_container,
                LoginFragment(),
                LoginFragment::class.java.simpleName
            )
            commit()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressbar.isVisible = isLoading
            btnRegister.isVisible = !isLoading
            edName.isEnabled = !isLoading
            edEmail.isEnabled = !isLoading
            edPassword.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun Button.setOnClickRegisterListenerWithValidation(
        edName: EditText,
        edEmail: EditText,
        edPassword: EditText,
        rgGender: RadioGroup,
        onSuccess: () -> Unit
    ) {
        setOnClickListener {
            when {
                edEmail.error.isNullOrEmpty().not() || edEmail.text.isNullOrEmpty() ->
                    showToast("Fill email correctly!")

                edName.text.isNullOrEmpty() -> showToast("Fill name correctly!")

                edPassword.error.isNullOrEmpty().not() || edPassword.text.isNullOrEmpty() ->
                    showToast("Fill password correctly!")

                rgGender.checkedRadioButtonId == -1 ->
                    showToast("Fill Gender Correctly!")

                else -> {
                    registerViewModel.register(
                        edName.text.toString(),
                        edEmail.text.toString(),
                        edPassword.text.toString(),
                        if (rgGender.checkedRadioButtonId == binding.rbMale.id) {
                            binding.rbMale.text.toString().uppercase()
                        } else {
                            binding.rbFemale.text.toString().uppercase()
                        }
                    )
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> registerViewModel.isLoading.postValue(true)
                                is Result.Success -> {
                                    registerViewModel.isLoading.postValue(false)
                                    registerViewModel.errorMessage.postValue(result.data.message)
                                    onSuccess()
                                }

                                is Result.Error -> {
                                    registerViewModel.isLoading.postValue(false)
                                    registerViewModel.errorMessage.postValue(result.error)
                                }
                            }
                        }
                }
            }
        }
    }
}