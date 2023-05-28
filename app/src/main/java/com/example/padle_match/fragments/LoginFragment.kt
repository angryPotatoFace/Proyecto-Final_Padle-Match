package com.example.padle_match.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import com.example.padle_match.databinding.FragmentLoginBinding

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

        binding.btnCreateAccount.setOnClickListener{
            val email =  binding.loginEmailInput.text.toString();
            val pass = binding.loginPassInput.text.toString();
            
            if( !email.isNullOrBlank() && !pass.isNullOrBlank() ) {

                lifecycleScope.launch {
                    val res = viewModel.loginUser(email,pass);
                    if( res ) {
                        val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
                        findNavController().navigate(action)
                    }else{
                        Snackbar.make(binding.root, "Error on authentification", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }else{
                Snackbar.make(binding.root, "Error password o email empty", Snackbar.LENGTH_SHORT).show()
            }

        }

    }

}