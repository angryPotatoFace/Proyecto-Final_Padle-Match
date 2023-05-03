package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.padle_match.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyClubsFragment : Fragment() {

    companion object {
        fun newInstance() = MyClubsFragment()
    }

    private lateinit var viewModel: MyClubsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_clubs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyClubsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}