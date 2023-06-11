package com.example.padle_match.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentAddClubBinding
import com.example.padle_match.entities.Club
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class AddClubFragment : Fragment() {

    private lateinit var binding: FragmentAddClubBinding
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var viewModel: AddClubViewModel

    companion object {
        fun newInstance() = AddClubFragment()
    }

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


            var clubs = viewModel.getPartidosList()
            val data = clubs.map { t -> t.data["nombre"] } as List<String>
            var list = data.toTypedArray();

            (binding.listaPartidos as? MaterialAutoCompleteTextView)?.setSimpleItems(list)

            binding.listaPartidos.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    binding.listaLocalidades.setText("")
                    val selecPartido = binding.listaPartidos.text.toString()
                    val selecClubs = clubs.filter { t -> t.data["nombre"] == selecPartido }

                    selecClubs.forEach{ t ->
                        val lista = t.data["localidades"] as List<String>
                        val localidades = lista.toTypedArray()
                        (binding.listaLocalidades as? MaterialAutoCompleteTextView)?.setSimpleItems(localidades)
                    }

                }


        }

        binding.btnAddClub.setOnClickListener {
            if (checkCredentials()) {
                handlerAddClub(binding.btnAddClub)
            }
        }

    }

    private fun handlerAddClub(btn: AppCompatButton) {
        val club = createClub();
        lifecycleScope.launch {
            var id = viewModel.addClub(club)
            club.id = id;
            viewModel.updateClub(club, id);
            findNavController().popBackStack(R.id.myClubsFragment, false)
            Snackbar.make(requireView(),"El club fue agregado con exito", Snackbar.LENGTH_LONG).show()
            cleanInputs()
        }
    }

    private fun createClub(): Club {

        val id = ""
        val nombre = binding.nombreEditText.text.toString();
        val cuit = binding.cuitEditText.text.toString();
        val provincia = "Buenos Aires";
        //binding.provinciaEditText.text.toString();
        val partido = binding.listaPartidos.text.toString();
        val localidad = "Agregar";
        val direccion = binding.direccionEditText.text.toString();
        val email = binding.emailEditText.text.toString();
        val telefono = binding.telefonoEditText.text.toString();
        val userId = auth.currentUser!!.uid

        val retorno = Club(
            id,
            nombre,
            cuit,
            provincia,
            partido,
            localidad,
            direccion,
            email,
            telefono,
            userId,
        )

        return retorno
    }

    private fun checkCredentials(): Boolean {
        var isValid = true

        // Validar campo Nombre
        if (!viewModel.checkedNoSpecialCharacters(binding.nombreEditText)) {
            isValid = false
        }

        // Validar campo Cuit
        if (!viewModel.checkedCuit(binding.cuitEditText)) {
            isValid = false
        }

        // Validar campo Partido
        if (!viewModel.checkedPartido(binding.listaPartidos, binding.listaPartidosTextInputLayout)) {
            isValid = false
        }

        //validar localidad

        // Validar campo Direccion
        if (!viewModel.checkedDireccion(binding.direccionEditText)) {
            isValid = false
        }

        // Validar campo Email
        if (!viewModel.checkedEmail(binding.emailEditText)) {
            isValid = false
        }

        // Validar campo Telefono
        if (!viewModel.checkedTelefono(binding.telefonoEditText)) {
            isValid = false
        }

        return isValid
    }
    private fun cleanInputs() {
        binding.nombreEditText.setText("")
        binding.cuitEditText.setText("")
        //binding.provinciaEditText.setText("Buenos Aires")
        binding.listaPartidos.setText("")
        binding.direccionEditText.setText("")
        binding.emailEditText.setText("")
        binding.telefonoEditText.setText("")
    }

}

