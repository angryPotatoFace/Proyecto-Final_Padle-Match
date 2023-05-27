package com.example.padle_match.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.padle_match.databinding.FragmentMyProfileBinding
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentClubDetailBinding
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.User
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch

class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()


        lifecycleScope.launch {

            val usuario = viewModel.getUser()

        }
    }
    private fun setValues(selected: User?) {
        selected?.let {
            binding.editTextMyProfileName.setText(it.nombre)
            binding.editTextMyProfileSurname.setText(it.apellido)
            binding.editTextMyProfileEmail.setText(it.email)
            binding.editTextMyProfilePhone.setText(it.telefono)
            binding.editTextMyProfileDni.setText(it.dni)
        }
    }

}