package com.example.padle_match.fragments

import android.icu.text.CaseMap.Lower
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.padle_match.entities.Tournament
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MyTournamentsViewModel: ViewModel() {

    private lateinit var tournamentList: MutableLiveData< MutableList<Tournament>>
    val db = Firebase.firestore

    private var auth: FirebaseAuth = Firebase.auth

    suspend fun getTournament(): MutableList<Tournament> {

        var list = mutableListOf<Tournament>();
        var uid = auth.currentUser!!.uid
        val documents = db.collection("tournaments").whereEqualTo("userId", uid).get().await()
        documents.forEach { data ->
            val id = data["id"] as? String?: "ID"
            val titulo = data["titulo"] as? String ?: "Torneo default"
            val club = data["club"] as? String ?: "Torneo default"
            val fecha = data["fecha"] as? String ?: "No se proporciono fecha"
            val hora = data["hora"] as? String ?: "No se proporciono hora"
            val cat = data["categoría"] as? String ?: "No se proporciono categoria"
            val cupos = data["cupos"] as? Number ?: 0
            val costoInscripción = data["costoInscripción"] as? Number ?: 0
            val material = data["materialCancha"] as? String ?: "No se proporciono premios"
            val premios = data["premios"] as? String ?: "No se proporciono premios"
            val imagenTorneo = data["imagenTorneo"] as? String ?: "No se proporciono imagenTorneo"
            val userId = data["userId"] as? String ?: ""
            val idClub = data["idClub"] as? String ?: ""


            val torneo =  Tournament(id, titulo, club, fecha, hora, cat, material, cupos, costoInscripción, premios, imagenTorneo, userId, idClub)
            list.add(torneo);
        }

        return list
    }
}