package com.example.padle_match.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.Tournament
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class MyTournamentsViewModel: ViewModel() {

    private val db = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun getTournament(): MutableList<Tournament> {
        val list = mutableListOf<Tournament>()
        val uid = auth.currentUser!!.uid
        val documents = db.collection("tournaments").whereEqualTo("userId", uid).get().await()

        for (data in documents) {
            val id = data["id"] as? String ?: "ID"
            val titulo = data["titulo"] as? String ?: "Torneo default"
            val fecha = data["fecha"] as? String ?: "No se proporciono fecha"
            val hora = data["hora"] as? String ?: "No se proporciono hora"
            val cat = data["categoría"] as? String ?: "No se proporciono categoria"
            val cupos = data["cupos"] as? Number ?: 0
            val costoInscripción = data["costoInscripción"] as? Number ?: 0
            val material = data["materialCancha"] as? String ?: "No se proporciono materialCancha"
            val premios = data["premios"] as? String ?: "No se proporciono premios"
            val imagenTorneo = data["imagenTorneo"] as? String ?: "No se proporciono imagenTorneo"
            val userId = data["userId"] as? String ?: ""
            val idClub = data["idClub"] as? String ?: ""
            val nombreCoor = data["nombreCoordinador"] as? String ?: ""
            val telefonoCood = data["telefonoCoordinador"] as? String ?: ""

            val torneo = Tournament(id, titulo, fecha, hora, cat, material, cupos, costoInscripción, premios, imagenTorneo, userId, idClub, nombreCoor, telefonoCood)

            // Obtener el nombre de usuario a través del userId
            val user = db.collection("users").document(userId).get().await()
            val username = user.getString("nombre") ?: "No se encontró el nombre de usuario"

            list.add(torneo)
        }

        return list
    }

    fun deleteOldTournaments() {
        val currentDate = Date()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Cambia el formato aquí

        db.collection("tournaments")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val dateString = document.getString("fecha")
                    val tournamentDate = format.parse(dateString)
                    if (tournamentDate != null && tournamentDate.before(currentDate)) {
                        db.collection("tournaments").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("DeleteSuccess","Successfully deleted old tournament with id: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("DeleteError", "Error deleting old tournament", e)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreError", "Error getting documents: ", exception)
            }
    }

}
