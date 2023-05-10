package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var v: View
    private lateinit var viewModel: LoginViewModel
    private lateinit var btnLogear: Button
    private lateinit var btnCreateAccount: TextView
    private lateinit var btnForgorPass: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_login, container, false)

        btnLogear = v.findViewById(R.id.btnEnviarEmail)
        btnCreateAccount = v.findViewById(R.id.txtLinkCrearCta)
        btnForgorPass = v.findViewById(R.id.tvOlividoContr)

        return v;
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

}