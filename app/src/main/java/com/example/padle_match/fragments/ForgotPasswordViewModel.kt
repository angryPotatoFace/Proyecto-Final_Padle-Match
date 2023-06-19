package com.example.padle_match.fragments

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


interface ForgotPasswordListener {
    fun onEmailSentAndComplete()
    fun onError(errorMsg : String)
}


class ForgotPasswordViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    var listener: ForgotPasswordListener? = null

    fun sendEmail(email: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                if (signInMethods.isNotEmpty()) {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { resetTask ->
                        if (resetTask.isSuccessful) {
                            listener?.onEmailSentAndComplete()
                        } else {
                            listener?.onError("Error al enviar el correo electrónico de cambio de contraseña")
                        }
                    }
                } else {
                    listener?.onError("El correo electrónico no está registrado")
                }
            }
        }
    }
}
