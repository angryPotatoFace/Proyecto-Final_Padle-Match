package com.example.padle_match.fragments

import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.*
import com.example.padle_match.entities.Club
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AddClubViewModel : ViewModel() {


    //
    val db = Firebase.firestore
    val storage = Firebase.storage(Firebase.app)
    private var auth: FirebaseAuth = Firebase.auth

    suspend fun getPartidosList(): QuerySnapshot {
        val query =  db.collection("partidos")
        val clubs = query.get().await();

        return clubs;
    }

    suspend fun addClub( club: Club, ): String {

        val query = db.collection("clubs")
        val data = query.add(club)
        var id = "NO HAY DATOS"

        data.addOnSuccessListener{ document ->
            id = document.id
        }.await()

        return id;
    }

    suspend fun updateClub( club: Club, id: String ) {
        val query = db.collection("clubs")
        val data = query.document(id).set(club)
        data.addOnSuccessListener { document ->
            Log.w("Update Club", "User ${id} was update correctly")
        }.await()
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

    fun checkedCuit(inputCuit: EditText): Boolean {
        return if(!checkedEmpty(inputCuit)){
            false
        } else if(!checkedMinLength(inputCuit, 11)){
            false
        } else {
            val cuitRegistered = runBlocking {cuitAlreadyRegistered(inputCuit.text.toString())}
            if(cuitRegistered){
                showError(inputCuit, "Cuit ya registrado")
                clearInput(inputCuit)
                false
            }
            else{
                true
            }
        }
    }

    fun checkedCuit(inputCuit: EditText, club: Club): Boolean {
        return if(!checkedEmpty(inputCuit)){
            false
        } else if(!checkedMinLength(inputCuit, 11)){
            false
        } else if(!inputCuit.text.toString().equals(club.cuit)) {
            val cuitRegistered = runBlocking {cuitAlreadyRegistered(inputCuit.text.toString())}
            if(cuitRegistered){
                showError(inputCuit, "Cuit ya registrado")
                clearInput(inputCuit)
                false
            }
            else{
                true
            }
        } else{
            true
        }
    }

    private suspend fun cuitAlreadyRegistered(cuit: String): Boolean {
        val currentUserId = getCurrentUserId()
        if (currentUserId != null) {
            return withContext(Dispatchers.IO) {
                val collection = db.collection("clubs")
                val query = collection.whereEqualTo("cuit", cuit)
                    .whereEqualTo("userId", currentUserId)
                    .limit(1)
                    .get()
                    .await()
                query.documents.isNotEmpty()
            }
        }
        return false
    }

    private suspend fun getCurrentUserId(): String? {
        return withContext(Dispatchers.IO) {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            user?.uid
        }
    }
    fun checkedPartido(listaPartidos: AutoCompleteTextView, listaPartidosTextInputLayout: TextInputLayout
    ): Boolean {
        return if(!checkedEmpty(listaPartidos, listaPartidosTextInputLayout)){
            false
        }else{
            listaPartidosTextInputLayout.error = null
            true
        }
    }
    fun checkedDireccion(direccionEditText: EditText): Boolean {
        return if(!checkedEmpty(direccionEditText)){
            false
        }else checkedMinLength(direccionEditText, 6)
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

    fun checkedEmail(inputEmail: EditText, selected : Club): Boolean {
        return if(!checkedEmpty(inputEmail)){
            false
        } else if(!checkedMinLength(inputEmail, 6)){
            false
        } else if (!isValidEmail(inputEmail.text.toString())) {
            showError(inputEmail, "Email inválido")
            false
        } else if(!inputEmail.text.toString().equals(selected.email)){
            val emailRegistered = runBlocking { emailAlreadyRegistered(inputEmail.text.toString()) }
            if (emailRegistered) {
                showError(inputEmail, "Email ya registrado")
                clearInput(inputEmail)
                false
            } else {
                true
            }
        } else{
            true
        }
    }



    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return email.matches(regex)
    }

    private suspend fun emailAlreadyRegistered(email: String): Boolean {
        val userId = getCurrentUserId()
        if (userId != null) {
            val querySnapshot = db.collection("clubs")
                .whereEqualTo("email", email)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            return !querySnapshot.isEmpty
        }
        return false
    }

    fun checkedTelefono(inputTelefono: EditText): Boolean {
        return if(!checkedEmpty(inputTelefono)){
            false
        }else checkedMinLength(inputTelefono, 10)
    }

    private fun checkedEmpty(editText: EditText): Boolean {
        var valid = true
        if(editText.text.isEmpty()){
            showError(editText,"Campo requerido" )
            valid = false
        }
        return valid
    }
    private fun checkedEmpty(editText: EditText, textInputLayout: TextInputLayout): Boolean {
        var valid = true
        if(editText.text.isEmpty()){
            showErrorTextInputLayout(textInputLayout,"Campo requerido" )
            valid = false
        }
        return valid
    }


    private fun checkedMinLength(editText: EditText, min : Int): Boolean {
        var valid = true
        if(editText.text.length < min){
            showError(editText,"El campo debe tener al menos $min carácteres" )
            valid = false
        }
        return valid
    }

    private fun isValidInput(input: String): Boolean {
        val regex = Regex("^[A-Za-záéíóúÁÉÍÓÚ]+(\\s[A-Za-záéíóúÁÉÍÓÚ]+)*$")
        return input.matches(regex)
    }

    private fun showError(editText: EditText, s: String) {
        editText.error = s
    }

    private fun showErrorTextInputLayout(textInputLayout: TextInputLayout, s: String) {
        textInputLayout.error = s
        textInputLayout.errorIconDrawable = null
    }

    private fun clearInput(editText: EditText) {
        editText.setText("")
    }


}

