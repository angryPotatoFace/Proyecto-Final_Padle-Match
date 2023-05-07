package com.example.padle_match.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.entities.TournamentRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyTournamentsFragment : Fragment() {

    private lateinit var v: View
    private var repository: TournamentRepository = TournamentRepository()
    private lateinit var adapter: TournamentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTournament: Button

    val db = Firebase.firestore
    // Create a new user with a first and last name

    val user = hashMapOf(
        "first" to "Ada",
        "last" to "Lovelace",
        "born" to 1815
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_my_tournaments, container, false)
        recyclerView = v.findViewById(R.id.tournament_list)
     //   btnAddTournament = v.findViewById(R.id.btn_add_tournament)

       /* db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            } */

        return v
    }

    override fun onStart() {
        super.onStart()
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TournamentAdapter(repository.getTournaments())
        recyclerView.adapter = adapter
    }

}