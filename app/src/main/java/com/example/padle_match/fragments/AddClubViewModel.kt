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
    val storageRef = storage.reference

    suspend fun getPartidosList(): Array<String> {

        val query =  db.collection("partidos")
        val clubs = query.get().await();
        val data = clubs.map { t -> t.data["nombre"] } as List<String>
        var list = data.toTypedArray();
        return list;
    }

    suspend fun addClub( club: Club, ): String {
        val query = db.collection("clubs")
        val data = query.add(club)
        var udi = "NO HAY DATOS"

        data.addOnSuccessListener{ document ->
            udi = document.id
        }.await()

        return udi;
    }

    suspend fun updateClub( club: Club, uid: String ){
        val query = db.collection("clubs")
        val data = query.document(uid).set(club)
        data.addOnSuccessListener{ document ->
            Log.w("Update Club", "User ${uid} was update correctly")
        }.await()
    }






}