package com.example.padle_match.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.entities.Tournament
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MyTournamentsFragment : Fragment() {

    private lateinit var v: View
    private lateinit var viewModel: MyTournamentsViewModel
    private lateinit var adapter: TournamentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTournament: FloatingActionButton
    private var list: MutableList<Tournament> = mutableListOf()

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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyTournamentsViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            // ESTO LLAMAR COROUTINA EN EL VIEWMODEL.
            list = viewModel.getTournament();
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = TournamentAdapter(list, requireContext()) { pos ->
                onItemClick(pos)
            }
            recyclerView.adapter = adapter
        }

        btnAddTournament.setOnClickListener{
            val action = MyTournamentsFragmentDirections.actionMyTournamentsFragmentToAddTournamentFragment()
            findNavController().navigate(action)
        }
    }

    fun onItemClick ( position : Int )  {
        Log.w("POSICION", position.toString())
        Log.w("LISTA", list.toString())
        val action = MyTournamentsFragmentDirections.actionMyTournamentsFragmentToTournamentDetailFragment(list[position])
        findNavController().navigate(action)
    }
}