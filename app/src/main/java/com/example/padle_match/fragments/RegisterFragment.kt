package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.entities.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel
    private lateinit var v : View;
    private lateinit var input_name: EditText
    private lateinit var input_apellido: EditText
    private lateinit var input_email: EditText
    private lateinit var input_Dni: EditText
    private lateinit var input_Telefono: EditText
    private lateinit var input_password: EditText
    private lateinit var input_ConfirPassword: EditText
    private lateinit var btnEnviar: Button




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_register, container, false)


        input_name = v.findViewById(R.id.etNombre)
        input_apellido = v.findViewById(R.id.etApellido)
        input_email = v.findViewById(R.id.etEmail)
        input_Dni = v.findViewById(R.id.etDni)
        input_Telefono = v.findViewById(R.id.etTelefono)
        input_password = v.findViewById(R.id.etContrasena)
        input_ConfirPassword = v.findViewById(R.id.etConfirContras)

        btnEnviar = v.findViewById(R.id.btnCrearCuenta)
        return v;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

    }

    override fun onStart() {
        super.onStart()

        btnEnviar.setOnClickListener{

                val name = input_name.text.toString();
                val last = input_apellido.text.toString()
                val email = input_email.text.toString();
                val dni = input_Dni.text.toString()
                val telefono = input_Telefono.text.toString();
                val pass = input_password.text.toString();
                val confir = input_ConfirPassword.text.toString();

                if ( pass != null && pass == confir) {
                    lifecycleScope.launch {
                        val user = viewModel.registerUser(email, pass);
                        val usuario = User( user.uid,name,last,email,telefono,dni );
                        viewModel.createUser( usuario);
                        clearInputs()

                        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }

                }else{
                    Snackbar.make(v, "Error on password are not equal", Snackbar.LENGTH_SHORT)
                }
        }


    }

    private fun clearInputs() {
        input_name.setText("")
        input_apellido.setText("")
        input_email.setText("")
        input_Dni.setText("")
        input_Telefono.setText("")
        input_password.setText("")
        input_ConfirPassword.setText("")


    }

}