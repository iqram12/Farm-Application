package com.dev.farmseller.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dev.farmseller.ui.BaseFragment
import com.dev.farmseller.R
import com.dev.farmseller.databinding.FragmentLoginBinding
import com.dev.farmseller.util.Resource
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {
    private val viewModel: LoginViewModel by viewModels()

    // Tracking the authentication state using live data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.loginState.observe(viewLifecycleOwner) { result ->
                when(result) {
                    is Resource.Success -> {
                        saveUserEmail(sharedPref)
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build()
                        // Once successfully logged in, the user is navigated to the the home screen
                        findNavController().safeNavigate(
                            LoginFragmentDirections.actionLoginFragmentToHomeFragment(),
                            navOptions
                        )
                    }
                    else -> {}
                }

            }
        }

        binding.loginBtn.setOnClickListener {
            validateAndLogin()
        }

        binding.registrationButton.setOnClickListener {
            findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment(), null)
        }
    }

    fun saveUserEmail(sharedPref: SharedPreferences) {
        val email = viewModel.auth.currentUser?.email
        email?.let {
            sharedPref.edit().putString("userEmail", it).apply()
        }
    }

    // Login validation structure taken from: https://stackoverflow.com/questions/49355508/how-to-validation-login-in-android-project
    private fun validateAndLogin(){
        val email = binding.emailEditText
        val password = binding.passwordEditText
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error = "Invalid Email Format!"
        }
        else if (TextUtils.isEmpty(password.text)){
            binding.passwordEditText.error = "Please enter your password!"
        }
        else {
            login()
        }
    }
    // When the user clicks on login -> get the email and password from two EditText views in the interface
    private fun login() = lifecycleScope.launch {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        // communicating with the viewmodel
        viewModel.login(email,password)
    }

    /* Checking once we open the login fragment if the user already logged in or not
    * if it is then navigates to the home fragment */
    override fun onStart() {
        super.onStart()
        val firebaseUser = viewModel.auth.currentUser
        if (firebaseUser != null) {
            saveUserEmail(sharedPref)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()
            findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment(), navOptions)
        }
    }
}