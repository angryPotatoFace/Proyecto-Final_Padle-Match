package com.example.padle_match.fragments

import android.content.ContentValues
import android.icu.text.CaseMap.Lower
import android.net.Uri
import android.security.identity.ResultData
import android.text.format.DateFormat
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.*
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class AddClubViewModel : ViewModel() {


    //
    val db = Firebase.firestore
    val storage = Firebase.storage(Firebase.app)
    private var auth: FirebaseAuth = Firebase.auth

    suspend fun getPartidosList(): Array<String> {
        var uid = auth.currentUser!!.uid
        val query =  db.collection("partidos")
        val clubs = query.get().await();
        val data = clubs.map { t -> t.data["nombre"] } as List<String>
        var list = data.toTypedArray();

        return list;
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


    fun checkedTelefono(inputTelefono: EditText): Boolean {
        return checkedEmpty(inputTelefono)
    }

    fun checkedCuit(inputCuit: EditText): Boolean {
        return if(!checkedEmpty(inputCuit)){
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

    private suspend fun cuitAlreadyRegistered(cuit: String): Boolean {
        return withContext(Dispatchers.IO) {
            val collection = db.collection("clubs")
            val query = collection.whereEqualTo("cuit", cuit).limit(1).get().await()
            query.documents.isNotEmpty()
        }
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
        return if(!checkedLongitud(inputEmail, 5)){
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

    fun checkedLongitud(inputNombre: EditText, minCaracter: Int): Boolean {
        return if(!checkedEmpty(inputNombre)){
            false
        } else if (inputNombre.text.toString().length < 3 ){
            showError(inputNombre, "El ingreso minimo de carácteres es " + minCaracter)
            false
        }
        else{
            true
        }
    }

}

