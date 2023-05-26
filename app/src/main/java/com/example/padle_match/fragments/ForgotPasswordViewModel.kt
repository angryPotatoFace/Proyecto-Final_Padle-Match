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
                    // El correo electrónico está presente en la base de datos de Firebase
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { resetTask ->
                        if (resetTask.isSuccessful) {
                            // El correo electrónico de cambio de contraseña se ha enviado correctamente
                            listener?.onEmailSentAndComplete()
                        } else {
                            // Hubo un error al enviar el correo electrónico de cambio de contraseña
                            listener?.onError("Error al enviar el correo electrónico de cambio de contraseña")
                        }
                    }
                } else {
                    // El correo electrónico no está registrado en la base de datos de Firebase
                    listener?.onError("El correo electrónico no está registrado")
                }
            } else {
                // Hubo un error al obtener los métodos de inicio de sesión del correo electrónico
                listener?.onError("Error al obtener los métodos de inicio de sesión del correo electrónico")
            }
        }
    }
}
