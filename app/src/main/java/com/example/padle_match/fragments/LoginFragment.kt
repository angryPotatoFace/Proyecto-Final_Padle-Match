package com.example.padle_match.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.txtLinkCrearCta.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)

        }

        binding.tvOlividoContr.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

    }

    @SuppressLint("ShowToast")
    override fun onStart() {
        super.onStart()

        var clearCredentials = clearCredentialsIfNeeded()

        binding.btnLogin.setOnClickListener{
            val email =  binding.loginEmailInput
            val pass = binding.loginPassInput
            val passInputLayout = binding.etPassword
            val emailInputLayout = binding.etEmail
            checkCredentials(email, pass, passInputLayout, emailInputLayout)
        }

        if (!clearCredentials) {
            loadSavedCredentials()
        }
    }



    fun checkCredentials(emailEditText: EditText, passEditText: EditText, passInputLayout: TextInputLayout, emailInputLayout: TextInputLayout){
        var isValid = true

        if (!viewModel.checkedEmail(emailEditText, emailInputLayout)) {
            isValid = false
        }

        if (!viewModel.checkedPassword(passEditText, passInputLayout)) {
            isValid = false
        }

        if(isValid){
            val email = emailEditText.text.toString()
            val pass = passEditText.text.toString()
            lifecycleScope.launch {
                val res = viewModel.loginUser(email,pass);
                if (res){
                    val email = emailEditText.text.toString()
                    val pass = passEditText.text.toString()

                    saveCredentials(email, pass)

                    val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
                    findNavController().navigate(action)
                } else{
                    Snackbar.make(binding.root, "Email o contraseña incorrectos.", Snackbar.LENGTH_SHORT).show()
                    passEditText.clearFocus()
                    emailEditText.clearFocus()
                    clearErrors(passInputLayout, emailInputLayout)
                    cleanInputs()
                }
            }
        }
    }

    private fun clearErrors(vararg textInputLayouts: TextInputLayout) {
        for (textInputLayout in textInputLayouts) {
            textInputLayout.error = null
            textInputLayout.errorIconDrawable = null
        }
    }
    private fun cleanInputs() {
        binding.loginEmailInput.setText("")
        binding.loginPassInput.setText("")
    }

    private fun saveCredentials(email: String, password: String) {
        val sharedPref = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun loadSavedCredentials() {
        val sharedPref = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)
        val password = sharedPref.getString("password", null)

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            binding.loginEmailInput.setText(email)
            binding.loginPassInput.setText(password)
        }
    }

    private fun clearCredentialsIfNeeded(): Boolean {
        val prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val clearCredentials = prefs.getBoolean("clear_credentials", false)
        if (clearCredentials) {
            cleanInputs()
            val credentialsPrefs = requireContext().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            with(credentialsPrefs.edit()) {
                remove("email")
                remove("password")
                apply()
            }
            with(prefs.edit()) {
                putBoolean("clear_credentials", false)
                apply()
            }
        }
        return clearCredentials
    }

}