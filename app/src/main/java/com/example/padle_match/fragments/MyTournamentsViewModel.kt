package com.example.padle_match.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.padle_match.entities.Tournament
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyTournamentsViewModel: ViewModel() {

    private lateinit var tournamentList: MutableLiveData< MutableList<Tournament>>
    val db = Firebase.firestore

            fun getTournament() : MutableLiveData< MutableList<Tournament> > {

                db.collection("tournaments")
                    .get()
                    .addOnSuccessListener { documents ->
                        documents.forEach { data ->
                            val d = data.data;
                            val id = data.id
                            val titulo = d["titulo"] as? String ?: "Torneo default"
                            val fecha = d["fecha"] as? String ?: "No se proporciono fecha"
                            val hora = d["hora"] as? String ?: "No se proporciono hora"
                            val categoría = d["hora"] as? String ?: "No se proporciono categoria"
                            val cupos = d["cupos"] as? Number ?: 0
                            val costoInscripción = d["costoInscripción"] as? Number ?: 0
                            val premios = d["premios"] as? String ?: "No se proporciono premios"
                            val imagenTorneo = d["imagenTorneo"] as? String ?: "No se proporciono imagenTorneo"

                            val torneo = Tournament(
                                id,
                                titulo,
                                fecha,
                                hora,
                                categoría,
                                cupos,
                                costoInscripción,
                                premios,
                                imagenTorneo
                            )
                            tournamentList.value?.add(torneo)
                        }
                    }

                return tournamentList
            }
}