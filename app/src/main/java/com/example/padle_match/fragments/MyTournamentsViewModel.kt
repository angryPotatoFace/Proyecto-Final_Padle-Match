package com.example.padle_match.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.padle_match.entities.Tournament
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MyTournamentsViewModel: ViewModel() {

    private lateinit var tournamentList: MutableLiveData< MutableList<Tournament>>
    val db = Firebase.firestore

    suspend fun getTournament(): MutableList<Tournament> {

        val list = mutableListOf<Tournament>();
        val documents = db.collection("tournaments").get().await()
        documents.forEach { data ->
            
            val titulo = data["titulo"] as? String ?: "Torneo default"
            val club = data["club"] as? String ?: "Torneo default"
            val fecha = data["fecha"] as? String ?: "No se proporciono fecha"
            val hora = data["hora"] as? String ?: "No se proporciono hora"
            val categoría = data["hora"] as? String ?: "No se proporciono categoria"
            val cupos = data["cupos"] as? Number ?: 0
            val costoInscripción = data["costoInscripción"] as? Number ?: 0
            val material = data["materialCancha"] as? String ?: "No se proporciono premios"
            val premios = data["premios"] as? String ?: "No se proporciono premios"
            val imagenTorneo = data["imagenTorneo"] as? String ?: "No se proporciono imagenTorneo"

            val torneo =  Tournament(titulo, club, fecha, hora, categoría, material, cupos, costoInscripción, premios, imagenTorneo)
            list.add(torneo);
        }

        return list
    }
}