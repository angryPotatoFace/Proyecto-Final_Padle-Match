package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.padle_match.R

class AddTournamentFragment : Fragment() {

    companion object {
        fun newInstance() = AddTournamentFragment()
    }

    private lateinit var viewModel: AddTournamentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_tournament, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTournamentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}