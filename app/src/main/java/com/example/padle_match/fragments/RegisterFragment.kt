package com.example.padle_match.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.entities.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout


class RegisterFragment : Fragment() {

    val db = Firebase.firestore

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
    private lateinit var textInputLayout_password: TextInputLayout
    private lateinit var input_ConfirPassword: EditText
    private lateinit var textInputLayout_confirPassword: TextInputLayout
    private lateinit var btnEnviar: Button
    private lateinit var txtVolver: TextView
    private lateinit var frameLayout: ConstraintLayout



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
        textInputLayout_password = v.findViewById(R.id.textInputLayout_password)
        input_ConfirPassword = v.findViewById(R.id.etConfirContras)
        textInputLayout_confirPassword = v.findViewById(R.id.textInputLayout_confirmPassword)
        frameLayout = v.findViewById(R.id.registerFrameLayout)
        btnEnviar = v.findViewById(R.id.btnCrearCuenta)
        txtVolver = v.findViewById(R.id.txtYaTieneCuenta)
        return v;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        setupFieldNavigation()

        setBackLogin()

        btnEnviar.setOnClickListener{

            val name = input_name.text.toString();
            val last = input_apellido.text.toString()
            val email = input_email.text.toString();
            val dni = input_Dni.text.toString()
            val telef = input_Telefono.text.toString();
            val telefono = asignarTelefono(telef)
            val pass = input_password.text.toString();

            lifecycleScope.launch {
                if(checkCredentials()){
                    val user = viewModel.registerUser(email, pass);
                    val usuario = User( user.uid,name,last,email,telefono,dni, "User created" );
                    viewModel.createUser(usuario);

                    with(requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).edit()) {
                        putBoolean("clear_credentials", true)
                        apply()
                    }

                    clearInputs()
                    findNavController().navigateUp()
                    Snackbar.make(requireView(), "Usuario creado con exito.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun asignarTelefono(telef: String): String {
        val telefono: String

        if (telef.startsWith("549")) {
            telefono = telef
        } else {
            telefono = "549$telef"
        }
        Log.d("Tel", telefono)
        return telefono
    }

    private fun setBackLogin() {
        txtVolver.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setupFieldNavigation() {
        input_name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                input_apellido.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        input_apellido.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                input_email.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        input_email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                input_Dni.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        input_Dni.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                input_Telefono.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        input_Telefono.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                input_password.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        input_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                input_ConfirPassword.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        // Para el último campo de entrada, es posible que desees hacer algo diferente, como cerrar el teclado virtual
        input_ConfirPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private suspend fun checkCredentials(): Boolean {
        var isValid = true

        // Validar campo Nombre
        if (!viewModel.checkedNoSpecialCharacters(input_name)) {
            isValid = false
        }

        // Validar campo Apellido
        if (!viewModel.checkedNoSpecialCharacters(input_apellido)) {
            isValid = false
        }

        // Validar campo Email
        if (!viewModel.checkedEmail(input_email)) {
            isValid = false
        }

        // Validar campo DNI
        if (!viewModel.checkedDNI(input_Dni)) {
            isValid = false
        }

        // Validar campo Telefono
        if (!viewModel.checkedTelefono(input_Telefono)) {
            isValid = false
        }

        // Validar campo Contraseña
        if (!viewModel.checkedPassword(input_password,textInputLayout_password)) {
            isValid = false
        }else{
            textInputLayout_password.error = null
        }

        // Validar campo Confirmar contraseña
        if (!viewModel.checkedConfirPassword(input_ConfirPassword, input_password, textInputLayout_confirPassword)) {
            isValid = false
        } else{
            textInputLayout_confirPassword.error = null
        }

        return isValid
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
