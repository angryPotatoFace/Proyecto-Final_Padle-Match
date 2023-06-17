package com.example.padle_match.fragments

import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.*
import com.example.padle_match.entities.Tournament
import com.example.padle_match.entities.User
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AddTournamentViewModel : ViewModel() {


    //
    val db = Firebase.firestore
    val storage = Firebase.storage(Firebase.app)
    val storageRef = storage.reference
    private var auth: FirebaseAuth = Firebase.auth


    companion object{
        private val MSG_CAMPO_REQUERIDO = "Campo requerido"
    }

    // Permite solo seleccionar una fecha a partir del dia actual

    fun createDatePicker(): MaterialDatePicker<Long> {
        val today = System.currentTimeMillis()
        return MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccione una fecha")
            .setSelection(today)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.from(today))
                    .build()
            )
            .build();
    }

    fun createTimePicker() : MaterialTimePicker{
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Seleccione una hora")
            .build()
    }
    suspend fun uploadImagenStorage( data: Uri, udi: String): String {

        var result: String = ""

        // Create the file metadata
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        // Upload file and metadata to the path 'images/mountains.jpg'
        val uploadTask = storageRef.child("images/${udi}/flyer/").putFile(data, metadata)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // Request for URL where the image is hosted
            storageRef.child("images/${udi}/flyer/").downloadUrl.addOnSuccessListener { uri ->
                result = uri.toString();
            }
        }.await()

        delay(1000);
        return result
    }

    suspend fun getClubsList(): Array<String> {
        var uid = auth.currentUser!!.uid
        val query =  db.collection("clubs").whereEqualTo("userId", uid)
        val clubs = query.get().await();
        val data = clubs.map { t -> t.data["nombre"] } as List<String>
        var list = data.toTypedArray();
        return list;
    }

    suspend fun getClubsIds(): List<String> {
        val query =  db.collection("clubs")
        val clubs = query.get().await();
        val data = clubs.map { t -> t.id }
        return data;
    }

    suspend fun getCategoriasList(): Array<String> {
        val query =  db.collection("categorias")
        val categorias = query.get().await();
        val data = categorias.map { t -> t.data["nombreCategoria"] }[0] as List<String>
        var list = data.toTypedArray()
        return list;
    }

    suspend fun getMaterialesList(): Array<String> {

        val query = db.collection("materialDeCancha")
        val materiales = query.get().await();
        val data = materiales.map { t -> t.data["materiales"] }[0] as List<String>
        var list = data.toTypedArray();

        return list;
    }

    suspend fun addTournament( tournament: Tournament, ): String {
        val query = db.collection("tournaments")
        val data = query.add(tournament)
        var udi = "NO HAY DATOS"

        data.addOnSuccessListener{ document ->
            udi = document.id
        }.await()

        return udi;
    }

    suspend fun updateTournament( tournament: Tournament, uid: String ){
        val query = db.collection("tournaments")
        val data = query.document(uid).set(tournament)
        data.addOnSuccessListener{ document ->
            Log.w("Update Tournament", "User ${uid} was update correctly")
        }.await()
    }


    suspend fun getIdClubByName( name: String): String{
        val query =  db.collection("clubs").whereEqualTo("nombre", name)
        var idClub = ""
        val categorias = query.get().await();

        categorias.forEach{data ->
            idClub = data.id
        }

        return idClub;
    }

    suspend fun getUser(): User {
        val id = auth.currentUser!!.uid
        val query =  db.collection("users").whereEqualTo("idUsuario", id)
        val resp = query.get().await()
        val data = resp.documents.get(0)
        var idUsuario= data!!["idUsuario"] as String
        var nombre= data!!["nombre"] as String
        var apellido= data!!["apellido"] as String
        var email= data!!["email"] as String
        var telefono= data!!["telefono"] as String
        var dni= data!!["dni"] as String
        var imgProfile= data!!["imgProfile"] as String

        return User(idUsuario,nombre,apellido,email,telefono,dni,imgProfile)
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
            hideError(editText)
            true
        }
    }

    private fun hideError(editText: EditText) {
        editText.error = null
    }

    private fun hideError(editText: EditText, textInputLayout: TextInputLayout) {
        editText.error = null
        textInputLayout.error = null
    }

    private fun checkedEmpty(editText: EditText): Boolean {
        var valid = true
        if(editText.text.isEmpty()){
            showError(editText,"Campo requerido" )
            valid = false
        }else{
            hideError(editText)
        }
        return valid
    }

    private fun checkedEmpty(editText: EditText, textInputLayout: TextInputLayout): Boolean {
        var valid = true
        if(editText.text.isEmpty()){
            showErrorTextInputLayout(textInputLayout,"Campo requerido" )
            valid = false
        }else{
            hideError(editText,textInputLayout)
        }
        return valid
    }

    private fun checkedMinLength(editText: EditText, min : Int): Boolean {
        var valid = true
        if(editText.text.length < min){
            showError(editText,"El campo debe tener al menos $min carácteres" )
            valid = false
        }else{
            hideError(editText)
        }
        return valid
    }

    private fun isValidInput(input: String): Boolean {
        val regex = Regex("^[A-Za-záéíóúÁÉÍÓÚ0-9]+(\\s[A-Za-záéíóúÁÉÍÓÚ0-9]+)*$")
        return input.matches(regex)
    }

    private fun showError(editText: EditText, s: String) {
        editText.error = s
    }

    private fun showErrorTextInputLayout(textInputLayout: TextInputLayout, s: String) {
        textInputLayout.error = s
        textInputLayout.errorIconDrawable = null
    }


    fun checkedClub(club: AutoCompleteTextView, textInputLayout: TextInputLayout): Boolean {
        return if(!checkedEmpty(club, textInputLayout)){
            false
        }else{
            hideError(club,textInputLayout)
            true
        }
    }

    fun checkedTelefono(inputTelefono: EditText): Boolean {
        return if(!checkedEmpty(inputTelefono)){
            false
        } else if(!checkedMinLength(inputTelefono, 10)){
            false
        } else{
            hideError(inputTelefono)
            true
        }
    }

    fun checkedRequired(editTextAddTournament: EditText, input: TextInputLayout): Boolean {
        return checkedEmpty(editTextAddTournament, input)
    }

    fun checkedPremio(editTextAddTournamentPremios: EditText): Boolean {
        return isValidInput(editTextAddTournamentPremios.text.toString())
    }


}