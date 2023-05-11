package com.example.padle_match.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.entities.Tournament
import com.google.android.material.snackbar.Snackbar


//import com.example.padle_match.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TournamentDetailFragment : Fragment() {

    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_tournament_detail, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()
        val tournamentSelected : Tournament = TournamentDetailFragmentArgs.fromBundle(requireArguments()).tournamentSelected
        Snackbar.make(v,tournamentSelected.titulo,Snackbar.LENGTH_SHORT).show()
    }
}