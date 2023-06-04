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
import com.example.padle_match.entities.User
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
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
import java.util.*

class AddTournamentViewModel : ViewModel() {


    //
    val db = Firebase.firestore
    val storage = Firebase.storage(Firebase.app)
    val storageRef = storage.reference
    private var auth: FirebaseAuth = Firebase.auth


    companion object{
        private val MSG_CAMPO_REQUERIDO = "Campo requerido"
    }
    fun validateFields(
        editName: EditText,
        editClub: AutoCompleteTextView,
        editFecha: EditText,
        editHorario: EditText,
        editCategorias: AutoCompleteTextView,
        editMateriales: AutoCompleteTextView,
        editCupo: EditText,
        editCosto: EditText,
        editPremios: EditText,
        editImagen: EditText
    ) : Boolean {
        return notEmpty(editName) && notEmpty(editClub) && notEmpty(editFecha) && notEmpty(editHorario) && notEmpty(editCategorias)
                && notEmpty(editMateriales) && notEmpty(editCupo) && notEmpty(editCosto) && notEmpty(editPremios) && notEmpty(editImagen)
    }

    private fun notEmpty(editText: EditText) : Boolean {
        var isValid : Boolean = true
        val value = editText.text.toString().trim()
        if(value.isNullOrEmpty()){
            editText.error = MSG_CAMPO_REQUERIDO
            isValid = false
        }
        return isValid
    }
    private fun notEmpty(autoCompleteTextView: AutoCompleteTextView): Boolean {
        val editText: EditText = autoCompleteTextView
        return notEmpty(editText)
    }

    // Permite solo seleccionar una fecha a partir del dia actual
    fun createDatePicker(): MaterialDatePicker<Long> {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
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
        val query =  db.collection("users").document(id)
        val resp = query.get().await()
        val data = resp.data
        var idUsuario= data!!["idUsuario"] as String
        var nombre= data!!["nombre"] as String
        var apellido= data!!["apellido"] as String
        var email= data!!["email"] as String
        var telefono= data!!["telefono"] as String
        var dni= data!!["dni"] as String
        var imgProfile= data!!["imgProfile"] as String

        return User(idUsuario,nombre,apellido,email,telefono,dni,imgProfile)
    }
}