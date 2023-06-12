package com.example.padle_match.fragments


import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class TournamentDetailFragmentViewModel : ViewModel() {

    val db = Firebase.firestore
    val storage = Firebase.storage(Firebase.app)
    val storageRef = storage.reference

    fun createDatePicker(): MaterialDatePicker<Long> {

        return MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccione una fecha")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build();
    }


    fun createHourPicker(): MaterialTimePicker {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Seleccione una hora")
                .build()
        return timePicker
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

        val query =  db.collection("clubs")
        val clubs = query.get().await();
        val data = clubs.map { t -> t.data["nombre"] } as List<String>
        var list = data.toTypedArray();
        return list;
    }

    suspend fun getCategoriasList(): Array<String> {

        val query =  db.collection("categorias")
        val categorias = query.get().await();
        val data = categorias.map { t -> t.data["nombreCategoria"] }[0] as List<String>
        var list = data.toTypedArray();
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
        var id = "NO HAY DATOS"

        data.addOnSuccessListener{ document ->
            id = document.id
        }.await()

        return id;
    }

    suspend fun updateTournament( tournament: Tournament, id: String ){
        val query = db.collection("tournaments")
        val data = query.document(id).set(tournament)
        data.addOnSuccessListener{ document ->
            Log.w("Update Tournament", "User ${id} was update correctly")
        }.await()
    }

    suspend fun deleteTournament( id: String ){
        val query = db.collection("tournaments")
        val data = query.document(id).delete()
        data.addOnSuccessListener{ document ->
            Log.w("Deleted Tournament", "Torneo ${id} was deleted correctly")
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

    suspend fun getIdClubById( id: String): Club {
        val query =  db.collection("clubs").whereEqualTo("id", id)
        val clubs = query.get().await();
        lateinit var retorno: Club


        clubs.forEach{data ->
            val id = data.data["id"] as String
            val nombre = data.data["nombre"] as String
            val cuit = data.data["cuit"] as String
            val provincia = data.data["provincia"] as String
            val partido = data.data["partido"] as String
            val localidad = data.data["localidad"] as String
            val direccion = data.data["id"] as String
            val email = data.data["email"] as String
            val telefono =  data.data["telefonos"] as String
            val userId = data.data["userId"] as String

            retorno = Club(
                id,
                nombre,
                cuit,
                provincia,
                partido,
                localidad,
                direccion,
                email,
                telefono,
                userId,
            )
        }

        return retorno;
    }
}