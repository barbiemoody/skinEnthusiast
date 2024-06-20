package com.capstone.skinenthusiast.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.capstone.skinenthusiast.R
import com.capstone.skinenthusiast.databinding.FragmentLoginBinding
import com.capstone.skinenthusiast.ui.auth.register.RegisterFragment
import com.capstone.skinenthusiast.ui.main.MainActivity
import com.capstone.skinenthusiast.utils.SettingsPreferences
import com.capstone.skinenthusiast.utils.Result
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModel<LoginViewModel>()

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        observeViewModel()

        setListeners()

        return binding.root
    }

    private fun observeViewModel() {
        with(loginViewModel) {
            isLoading.observe(viewLifecycleOwner, ::showLoading)
            errorMessage.observe(viewLifecycleOwner, ::showToast)
            getToken().observe(viewLifecycleOwner) {
                if (it != SettingsPreferences.PREFERENCE_DEFAULT_VALUE) {
                    val iMain = Intent(requireActivity(), MainActivity::class.java)
                    requireActivity().finishAffinity()
                    startActivity(iMain)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnLogin.setOnClickLoginListenerWithValidation(
                edEmail = binding.edEmail,
                edPassword = binding.edPassword
            )

            btnRegister.setOnClickListener {
                switchToRegister()
            }
        }
    }

    private fun switchToRegister() {
        with(parentFragmentManager.beginTransaction()) {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            replace(
                R.id.auth_container,
                RegisterFragment(),
                RegisterFragment::class.java.simpleName
            )
            commit()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressbar.isVisible = isLoading
            btnLogin.isVisible = !isLoading
            edEmail.isEnabled = !isLoading
            edPassword.isEnabled = !isLoading
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun Button.setOnClickLoginListenerWithValidation(
        edEmail: EditText,
        edPassword: EditText
    ) {
        setOnClickListener {
            when {
                edEmail.error.isNullOrEmpty().not() || edEmail.text.isNullOrEmpty() ->
                    showToast("Fill email correctly!")

                edPassword.error.isNullOrEmpty().not() || edPassword.text.isNullOrEmpty() ->
                    showToast("Fill password correctly!")

                else -> {
                    loginViewModel.login(edEmail.text.toString(), edPassword.text.toString())
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Result.Loading -> loginViewModel.isLoading.postValue(true)
                                is Result.Success -> {
                                    loginViewModel.isLoading.postValue(false)

                                    result.data.user?.let {
                                        loginViewModel.saveToken(it.token ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE)
                                        loginViewModel.saveAccountData(
                                            it.name ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.email ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.gender ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE,
                                            it.image ?: SettingsPreferences.PREFERENCE_DEFAULT_VALUE
                                        )
                                    }

                                    android.util.Log.e("FTEST", "token -> ${result.data.user?.token}")
                                }

                                is Result.Error -> {
                                    loginViewModel.isLoading.postValue(false)
                                    loginViewModel.errorMessage.postValue(result.error)
                                }
                            }
                        }
                }
            }
        }
    }
}