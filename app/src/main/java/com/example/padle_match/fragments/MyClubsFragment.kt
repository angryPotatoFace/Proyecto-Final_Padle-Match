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
import com.example.padle_match.adapter.ClubAdapter
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.Tournament
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MyClubsFragment : Fragment() {

    private lateinit var v: View
    private lateinit var viewModel: MyClubsViewModel
    private lateinit var adapter: ClubAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddClub: FloatingActionButton
    private var list: MutableList<Club> = mutableListOf()

    // Create connection with the database
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_my_clubs, container, false)
        recyclerView = v.findViewById(R.id.club_list)
        btnAddClub = v.findViewById(R.id.add_new_club)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyClubsViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            list = viewModel.getClub();
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = ClubAdapter(list, requireContext()) { pos ->
                onItemClick(pos)
            }
            recyclerView.adapter = adapter
        }

        btnAddClub.setOnClickListener{
            val action = MyClubsFragmentDirections.actionMyClubsFragmentToAddClubFragment()
            findNavController().navigate(action)
        }
    }
    fun onItemClick ( position : Int )  {
        Log.w("POSICION", position.toString())
        Log.w("LISTA", list.toString())
        val action = MyClubsFragmentDirections.actionMyClubsFragmentToClubDetailFragment(list[position])
        findNavController().navigate(action)
    }

    }