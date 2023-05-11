package com.example.padle_match.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.entities.Tournament
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MyTournamentsFragment : Fragment() {

    private lateinit var v: View
    // private var repository: TournamentRepository = TournamentRepository()
    private lateinit var adapter: TournamentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTournament: Button
    // private val viewModel = ViewModelProvider(this).get(MyTournamentsViewModel::class.java)
    private val list: MutableList<Tournament> = mutableListOf()

    // Create connection with the database
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_my_tournaments, container, false)
        recyclerView = v.findViewById(R.id.tournament_list)
        btnAddTournament = v.findViewById(R.id.add_new_tournament)

        return v
    }

    override fun onStart() {
        super.onStart()



        db.collection("tournaments")
            .get()
            .addOnSuccessListener { documents ->
               // val listaTorneos: MutableList<Tournament> = documents.toObjects(Tournament::class.java)

                documents.forEach{ data ->
                    Log.w("INFORMACION", data.data.toString())
                    val d = data.data;
                    val id = data.id
                    val titulo = d["titulo"] as? String?: "Torneo default"
                    val fecha = d["fecha"] as? String?: "No se proporciono fecha"
                    val hora = d["hora"] as? String?: "No se proporciono hora"
                    val categoría = d["categoria"] as? ArrayList<String>?: "No se proporciono categoria"
                    val cupos = d["cupos"] as? Number?: 0
                    val costoInscripción = d["costoInscripción"] as? Number?: 0
                    val premios = d["premios"] as? String?: "No se proporciono premios"
                    val imagenTorneo = d["imagenTorneo"] as? String?: "No se proporciono imagenTorneo"
                    val torneo = Tournament(id,titulo,fecha,hora,categoría.toString(),cupos, costoInscripción, premios,imagenTorneo)
                    list.add(torneo)
                }

                recyclerView.layoutManager = LinearLayoutManager(context)
                adapter = TournamentAdapter(list, requireContext()) { pos ->
                    onItemClick(pos)
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        btnAddTournament.setOnClickListener{
            val action = MyTournamentsFragmentDirections.actionMyTournamentsFragmentToAddTournamentFragment()
            findNavController().navigate(action)
        }
    }

    fun onItemClick ( position : Int )  {
        val action = MyTournamentsFragmentDirections.actionMyTournamentsFragmentToTournamentDetailFragment(list[position])
        findNavController().navigate(action)
    }
}