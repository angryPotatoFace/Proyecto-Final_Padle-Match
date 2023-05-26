package com.example.padle_match.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.Tournament
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MyClubsViewModel : ViewModel() {
    private lateinit var clubList: MutableLiveData<MutableList<Club>>
    val db = Firebase.firestore

    private var auth: FirebaseAuth = Firebase.auth

    suspend fun getClub(): MutableList<Club> {

        var list = mutableListOf<Club>();
        var uid = auth.currentUser!!.uid
        val documents = db.collection("clubs").whereEqualTo("id", uid).get().await()
        documents.forEach { data ->
            val nombre = data["nombre"] as? String ?: "nombre default"
            val cuit = data["cuit"] as? String ?: "cuit default"
            val provincia = data["provincia"] as? String ?: "Buenos Aires"
            val partido = data["partido"] as? String ?: "partido default"
            val localidad = data["localidad"] as? String ?: "localidad default"
            val direccion = data["direccion"] as? String ?: "No se proporciono direccion"
            val email = data["email"] as? String ?: "No se proporciono email"
            val telefono = data["telefono"] as? String ?: "No se proporciono telefono"
            val uid = data["uid"] as? String ?: ""

            val club =  Club(nombre,cuit,provincia,partido,localidad,direccion,email,telefono,uid)
            list.add(club);
        }

        return list
    }
}