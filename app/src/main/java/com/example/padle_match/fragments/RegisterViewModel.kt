package com.example.padle_match.fragments

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {

    val db = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth
    suspend fun registerUser(email: String, password: String ): FirebaseUser {
        lateinit var user: FirebaseUser

        try{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.w("REGISTER", "User Created")
                    } else {
                        Log.w("REGISTER", "createUserWithEmail:failure", task.exception)
                    }
                }.await()

        }catch (e: Exception) {
            Log.w("REGISTER", "createUserWithEmail:failure")
        }

        user = auth.currentUser!!;

        Log.w("REGISTER", "User")
        Log.w("REGISTER", user.toString())
        return user;
    }

    suspend fun createUser(usuario: User) {
        try{
            val query = db.collection("users")
            val data = query.add(usuario).await()
            Log.w("Creating User", "The user ${usuario.idUsuario} was created correctily" )
        }catch (e: Exception){
            Log.w("Creating User", "Occurred a error creating user" )
        }
    }

    fun checkedConfirPassword(inputConfirpassword: EditText, input_password: EditText): Boolean {
        return if(!checkedEmpty(inputConfirpassword)){
            false
        }else if(!checkedMinLength(inputConfirpassword, 6)){
            false
        } else if (!inputConfirpassword.text.toString().equals(input_password.text.toString())){
            showError(inputConfirpassword, "Las contraseñas no coinciden")
            false
        } else{
            true
        }
    }

    fun checkedPassword(inputPassword: EditText): Boolean {
        return if(!checkedEmpty(inputPassword)){
            false
        } else checkedMinLength(inputPassword, 6)
    }

    fun checkedTelefono(inputTelefono: EditText): Boolean {
        return if(!checkedEmpty(inputTelefono)){
            false
        } else checkedMinLength(inputTelefono, 10)
    }

    fun checkedDNI(inputDni: EditText): Boolean {
        return if(!checkedEmpty(inputDni)){
            false
        } else if(!checkedMinLength(inputDni, 7)){
            false
        } else {
            val dniRegistered = runBlocking {dniAlreadyRegistered(inputDni.text.toString())}
            if(dniRegistered){
                showError(inputDni, "DNI ya registrado")
                clearInput(inputDni)
                false
            }
            else{
                true
            }
        }
    }

    fun checkedDNI(inputDni: EditText, DNI : String): Boolean {
        return if(!checkedEmpty(inputDni)){
            false
        } else if(!checkedMinLength(inputDni, 7)){
            false
        } else if(!inputDni.text.toString().equals(DNI)) {
            val dniRegistered = runBlocking { dniAlreadyRegistered(inputDni.text.toString()) }
            if (dniRegistered) {
                showError(inputDni, "DNI ya registrado")
                clearInput(inputDni)
                false
            } else {
                true
            }
        } else{
            true
        }
    }


    private suspend fun dniAlreadyRegistered(dni: String): Boolean {
        return withContext(Dispatchers.IO) {
            val collection = db.collection("users")
            val query = collection.whereEqualTo("dni", dni).limit(1).get().await()
            query.documents.isNotEmpty()
        }
    }

    private fun checkedMinLength(editText: EditText, min : Int): Boolean {
        var valid = true
        if(editText.text.length < min){
            showError(editText,"El campo debe tener al menos $min carácteres" )
            valid = false
        }
        return valid
    }

    private fun checkedEmpty(editText: EditText): Boolean {
        var valid = true
        if(editText.text.isEmpty()){
            showError(editText,"Campo requerido" )
            valid = false
        }
        return valid
    }


    fun checkedEmail(inputEmail: EditText): Boolean {
        return if(!checkedEmpty(inputEmail)){
            false
        } else if(!checkedMinLength(inputEmail, 6)){
            false
        } else if (!isValidEmail(inputEmail.text.toString())) {
            showError(inputEmail, "Email inválido")
            false
        } else {
            val emailRegistered = runBlocking { emailAlreadyRegistered(inputEmail.text.toString()) }
            if (emailRegistered) {
                showError(inputEmail, "Email ya registrado")
                clearInput(inputEmail)
                false
            } else {
                true
            }
        }
    }

    private suspend fun emailAlreadyRegistered(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            val auth = FirebaseAuth.getInstance()
            try {
                val result = auth.fetchSignInMethodsForEmail(email).await()
                val signInMethods = result.signInMethods
                signInMethods != null && signInMethods.isNotEmpty()
            } catch (e: Exception) {
                // Error al verificar el correo electrónico
                // Manejar el error según sea necesario
                false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return email.matches(regex)
    }

    fun checkedNoSpecialCharacters(editText: EditText): Boolean {
        return if(!checkedEmpty(editText)){
            false
        }else if(!checkedMinLength(editText, 3)){
            false
        }else if(!isValidInput(editText.text.toString())){
            showError(editText, "Campo inválido")
            false
        } else{
            true
        }
    }


    private fun isValidInput(input: String): Boolean {
        val regex = Regex("^[A-Za-záéíóúÁÉÍÓÚ]+$")
        return input.matches(regex)
    }

    private fun showError(editText: EditText, s: String) {
        editText.error = s
        editText.clearFocus()
    }

    private fun clearInput(editText: EditText) {
        editText.setText("")
    }

}