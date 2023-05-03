package com.example.padle_match.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.entities.TournamentRepository

class MyTournamentsFragment : Fragment() {

    private lateinit var v: View
    private var repository: TournamentRepository = TournamentRepository()
    private lateinit var adapter: TournamentAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_my_tournaments, container, false)
        recyclerView = v.findViewById(R.id.tournament_list)

        return v
    }

    override fun onStart() {
        super.onStart()
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TournamentAdapter(repository.getTournaments())
        recyclerView.adapter = adapter
    }

}