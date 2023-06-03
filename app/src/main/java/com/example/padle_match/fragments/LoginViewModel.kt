package com.example.padle_match.fragments

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private var auth: FirebaseAuth = Firebase.auth


class LoginViewModel : ViewModel() {

    suspend fun loginUser(email: String, password: String): Boolean {
        var res = false;
        try{
            val request = auth.signInWithEmailAndPassword(email, password).await()
            res = true;
        }catch (e: Exception) {
            Log.w("Login Method", "signInWithEmail:failure");
        }
        return res
    }

     fun checkedEmail(inputEmail: EditText, emailInputLayout : TextInputLayout): Boolean{
        return if(!checkedEmpty(inputEmail, emailInputLayout)){
            false
        } else if (!isValidEmail(inputEmail.text.toString())){
            showErrorTextInputLayout(emailInputLayout, "El email es inválido")
            false
        } else{
            true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return email.matches(emailRegex)
    }

     fun checkedPassword(passEditText: EditText, passInputLayout : TextInputLayout): Boolean {
        return if (passEditText.text.toString().length < 6){
            showErrorTextInputLayout(passInputLayout, "La contraseña debe tener al menos 6 carácteres")
            false
        } else{
            true
        }
    }

    private fun checkedEmpty(editText: EditText, textInputLayout: TextInputLayout): Boolean {
        var valid = true
        if(editText.text.isEmpty()){
            showErrorTextInputLayout(textInputLayout,"Campo requerido" )
            valid = false
        }
        return valid
    }

    private fun showErrorTextInputLayout(textInputLayout: TextInputLayout, s: String) {
        textInputLayout.error = s
        textInputLayout.errorIconDrawable = null
    }

}