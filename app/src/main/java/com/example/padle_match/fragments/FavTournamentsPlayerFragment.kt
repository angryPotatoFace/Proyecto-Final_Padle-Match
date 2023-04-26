package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.padle_match.R

class FavTournamentsPlayerFragment : Fragment() {

    companion object {
        fun newInstance() = FavTournamentsPlayerFragment()
    }

    private lateinit var viewModel: FavTournamentsPlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fav_tournaments_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavTournamentsPlayerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}