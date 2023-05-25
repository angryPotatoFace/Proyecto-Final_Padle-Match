package com.example.padle_match.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
    private lateinit var v: View
    private lateinit var viewModel: LoginViewModel
    private lateinit var btnLogear: Button
    private lateinit var btnCreateAccount: TextView
    private lateinit var btnForgorPass: TextView
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPass: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_login, container, false)
            LoginFragment

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        btnCreateAccount = v.findViewById(R.id.txtLinkCrearCta)
        btnForgorPass = v.findViewById(R.id.tvOlividoContr)

        inputEmail = v.findViewById(R.id.login_email_input)
        inputPass = v.findViewById(R.id.login_pass_input)

        return binding.root;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        btnLogear.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToMyTournamentsFragment()
            findNavController().navigate(action)
        }

        btnCreateAccount.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        btnForgorPass.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

    }


    @SuppressLint("ShowToast")
    override fun onStart() {
        super.onStart()

        binding.btnCreateAccount.setOnClickListener{
            val email = inputEmail.text.toString();
            val pass = inputPass.text.toString();

            if( !email.isNullOrBlank() && !pass.isNullOrBlank() ) {

                lifecycleScope.launch {
                    viewModel.loginUser(email,pass);
                    val user = viewModel.currentUser();
                    if( ! user!!.uid.isNullOrBlank() ) {
                        val action = LoginFragmentDirections.actionLoginFragmentToMyTournamentsFragment()
                        findNavController().navigate(action)
                    }else{
                        Snackbar.make(v, "Error on authentification", Snackbar.LENGTH_SHORT).show()
                    }
                }

            }else{
                Snackbar.make(v, "Error password o email empty", Snackbar.LENGTH_SHORT).show()
            }

        }

    }

}