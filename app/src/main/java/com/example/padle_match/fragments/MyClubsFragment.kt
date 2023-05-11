package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyClubsFragment : Fragment() {

    companion object {
        fun newInstance() = MyClubsFragment()
    }
    private lateinit var viewModel: MyClubsViewModel
    lateinit var v : View
    lateinit var btnNavigate : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v =inflater.inflate(R.layout.fragment_my_clubs, container, false)
        btnNavigate = v.findViewById(R.id.button)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyClubsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        btnNavigate.setOnClickListener {
            val action = MyClubsFragmentDirections.actionMyClubsFragmentToClubDetailFragment()
            findNavController().navigate(action)
        }
    }
}