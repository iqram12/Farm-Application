package com.dev.farmbuyer.ui.register

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev.farmbuyer.ui.BaseFragment
import com.dev.farmbuyer.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(
    FragmentRegisterBinding::inflate
) {
    private val viewModel: RegisterViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registeredOrNot()
        binding.registerBtn.setOnClickListener {
            validateAndRegister()
        }
        binding.loginButton.setOnClickListener {
            findNavController().safeNavigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(), null)
        }
    }

    /*Similar structure to login learnt from: https://stackoverflow.com/questions/49355508/how-to-validation-login-in-android-project
       * Regex taken from: https://codingwitht.com/registration-form-validation-in-android-studio/ */

    private fun validateAndRegister(){
        val email = binding.emailEditText
        val userName = binding.userNameEditText
        val password = binding.passwordEditText

        // If email doesn't match then...
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error = "Invalid Email format"
        }
        /* We want the password to contain at least capital letter */
        else if (userName.text?.contains(" ") == true){
            userName.error = "User name must not contain spaces"
        }
        else if (password.text?.contains(Regex("[A-Z]")) != true){
            password.error = "Password must contain one capital letter"
        }
        /* We want the password to contain at least one number */
        else if (password.text?.contains(Regex("[a-z]")) != true){
            password.error = "Password must contain one small letter"
        }
        else if (password.text?.contains(Regex("[0-9]")) != true){
            password.error = "Password must contain one digit"
        }
        /* All Above conditions are true and we can continue */
        else {
            register()
            findNavController().safeNavigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(), null)
        }
    }

    private fun register(){
        val email = binding.emailEditText.text?.trim().toString()
        val userName = binding.userNameEditText.text?.trim().toString()
        val phoneNumber = binding.phoneNumberEditText.text?.trim().toString()
        val password = binding.passwordEditText.text?.trim().toString()
        viewModel.registration(
            registerEmail = email,
            registerUserName = userName,
            registerPhoneNumber = phoneNumber,
            registerPassword = password)
    }

    private fun registeredOrNot() = lifecycleScope.launch {
        viewModel.registerStatus.observe(viewLifecycleOwner) {
            /* If the live data is true...*/
            if (it) {
                findNavController().safeNavigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(), null)
            }
        }
    }
}