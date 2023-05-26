package com.example.padle_match.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentAddClubBinding
import com.example.padle_match.databinding.FragmentAddTournamentBinding
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*

class AddClubFragment : Fragment() {

    private lateinit var binding: FragmentAddClubBinding
    private var auth: FirebaseAuth = Firebase.auth

    companion object {
        fun newInstance() = AddClubFragment()
    }

    private lateinit var viewModel: AddClubViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddClubBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddClubViewModel::class.java)

    }


    override fun onStart() {
        super.onStart()


        lifecycleScope.launch {

            var partidosList = binding.partidoEditText
            var data = viewModel.getPartidosList()
            (partidosList as? MaterialAutoCompleteTextView)?.setSimpleItems(data);


        }

        handlerAddClub(binding.btnAddClub)
    }

    private fun handlerAddClub(btn: AppCompatButton) {
        btn.setOnClickListener {
            val club = createClub();

            lifecycleScope.launch {
                var udi = viewModel.addClub(club)
                viewModel.updateClub(club, udi);
                findNavController().popBackStack(R.id.myClubsFragment, false)
                Snackbar.make(requireView(),"El club fue agregado con exito", Snackbar.LENGTH_LONG).show()
                cleanInputs()
            }
        }
    }

    private fun cleanInputs() {
        binding.nombreEditText.setText("")
        binding.cuitEditText.setText("")
        binding.provinciaEditText.setText("Buenos Aires")
        binding.partidoEditText.setText("")
        binding.direccionEditText.setText("")
        binding.emailEditText.setText("")
        binding.telefonoEditText.setText("")
    }

    private fun createClub(): Club {

        val nombre = binding.nombreEditText.text.toString();
        val cuit = binding.cuitEditText.text.toString();
        val provincia = binding.provinciaEditText.text.toString();
        val partido = binding.partidoEditText.text.toString();
        val localidad = "Agregar";
        val direccion = binding.direccionEditText.text.toString();
        val email = binding.emailEditText.text.toString();
        val telefono = binding.telefonoEditText.text.toString();
        val udi = auth.currentUser!!.uid

        val retorno = Club(
            nombre,
            cuit,
            provincia,
            partido,
            localidad,
            direccion,
            email,
            telefono,
            udi,
        )

        return retorno
    }

}

