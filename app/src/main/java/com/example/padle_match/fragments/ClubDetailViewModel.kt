package com.example.padle_match.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padle_match.entities.Club
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClubDetailViewModel : ViewModel() {

    val db = Firebase.firestore

    suspend fun getPartidosList(): QuerySnapshot {

        val query =  db.collection("partidos")
        val clubs = query.get().await();

        return clubs;
    }

    suspend fun updateClub(club: Club, id: String ) {
        val query = db.collection("clubs")
        val data = query.document(id).set(club)
        data.addOnSuccessListener { document ->
            Log.w("Update Club", "Club ${id} was update correctly")
        }.await()
    }

    suspend fun deleteClub( id: String ){
        val query = db.collection("clubs")
        val data = query.document(id).delete()
        data.addOnSuccessListener{ document ->
            Log.w("Deleted Club", "Club ${id} was deleted correctly")
        }.await()
    }

    suspend fun deleteTournamentAssociate( id: String ) {
        val query = db.collection("tournaments")
        val data = query.get().await()
        val tournaments = data.filter { t -> t.data["idClub"] == id }
        tournaments.forEach(){ t ->
            viewModelScope.launch{
                deleteTournament(t.id)
            }
        }
    }
    suspend fun deleteTournament( id: String ){
        val query = db.collection("tournaments")
        val data = query.document(id).delete()
        data.addOnSuccessListener{ document ->
            Log.w("Deleted Tournament", "Torneo ${id} was deleted correctly")
        }.await()
    }

}