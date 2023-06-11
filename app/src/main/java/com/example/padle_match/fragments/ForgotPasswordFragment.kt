package com.example.padle_match.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class ForgotPasswordFragment : Fragment(), ForgotPasswordListener {

    private lateinit var binding : FragmentForgotPasswordBinding

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.btnSendEmail.setOnClickListener {
            if(checkCredentials()){
                binding.textInputLayoutEmailRecoverPassword.error = null
                viewModel.sendEmail(binding.editTextForgotPassword.text.toString())
            }
        }
    }

    private fun checkCredentials(): Boolean {
        var isValid = true
        val LoginViewModel : LoginViewModel by viewModels()

        if(!LoginViewModel.checkedEmail(binding.editTextForgotPassword, binding.textInputLayoutEmailRecoverPassword)){
            isValid = false
        }

        return isValid
    }

    override fun onEmailSentAndComplete() {
        val navController = findNavController()
        navController.popBackStack(R.id.loginFragment, false)
        Snackbar.make(requireView(), "Email de cambio de contraseña enviado.", Snackbar.LENGTH_LONG).show()
    }

    override fun onError(errorMessage: String) {
        binding.editTextForgotPassword.setText("")
        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        viewModel.listener = this
        // TODO: Use the ViewModel
    }

}