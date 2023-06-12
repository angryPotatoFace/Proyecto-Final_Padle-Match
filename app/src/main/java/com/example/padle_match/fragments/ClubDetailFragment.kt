package com.example.padle_match.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ViewSwitcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentAddClubBinding
import com.example.padle_match.entities.Club
import com.google.android.material.snackbar.Snackbar
import com.example.padle_match.databinding.FragmentClubDetailBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch


class ClubDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ClubDetailFragment()
    }

    private lateinit var binding: FragmentClubDetailBinding
    private lateinit var viewModel: ClubDetailViewModel
    private lateinit var selected: Club

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClubDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ClubDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        selected = ClubDetailFragmentArgs.fromBundle(requireArguments()).clubSelected
        setValues( selected )
        handlerEdit()
        handlerCancel(selected)
        handlerSave()
        handlerDelete()
        handlerBack()

        lifecycleScope.launch {
            // var data = viewModel.getPartidosList()
            // ( binding.editTextPartido as? MaterialAutoCompleteTextView)?.setSimpleItems(data)

            var clubs = viewModel.getPartidosList()
            val data = clubs.map { t -> t.data["nombre"] } as List<String>
            var list = data.toTypedArray();

            (binding.editTextPartido as? MaterialAutoCompleteTextView)?.setSimpleItems(list)

            binding.editTextPartido.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    binding.editTextLocalidades.setText("")
                    val selecPartido = binding.editTextPartido.text.toString()
                    val selecClubs = clubs.filter { t -> t.data["nombre"] == selecPartido }

                    selecClubs.forEach{ t ->
                        val lista = t.data["localidades"] as List<String>
                        Log.w("LISTA DE LOCALIDADES", lista.toString())
                        val localidades = lista.toTypedArray()
                        (binding.editTextLocalidades as? MaterialAutoCompleteTextView)?.setSimpleItems(localidades)
                    }

                }
        }

    }

    private fun handlerBack() {
        binding.btnBackClubDetail.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun handlerSave() {
        binding.saveButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro de aplicar los cambios realizados?")
                .setPositiveButton("SI") { _, _ ->
                    if(checkCredentials()){
                        lifecycleScope.launch {
                            val club = createClub()
                            viewModel.updateClub(club, club.id)
                        }
                        findNavController().popBackStack(R.id.myClubsFragment, false)
                        Snackbar.make( requireView(), "El club fue modificado con exito.", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun setValues(selected: Club) {
        binding.editTextNombre.setText( selected.nombre)
        binding.editTextCuit.setText( selected.cuit)
        binding.editTextPartido.setText( selected.partido)
        binding.editTextLocalidades.setText( selected.localidad)
        binding.editTextDirecciN.setText( selected.domicilio)
        binding.editTextEmail.setText( selected.email)
        binding.editTextTelefono.setText( selected.telefonos)
    }

    private fun handlerCancel(selected: Club) {
        binding.cancelButton.setOnClickListener {
            binding.viewSwitcher.showPrevious()
            binding.editTextNombre.setText(selected.nombre)
            binding.editTextNombre.isEnabled = false
            binding.editTextNombre.error = null
            binding.textInputLayoutNombre.clearFocus()
            binding.editTextCuit.setText(selected.cuit)
            binding.editTextCuit.isEnabled = false
            binding.editTextCuit.error = null
            binding.textInputLayoutCuit.clearFocus()
            binding.editTextPartido.setText(selected.partido)
            binding.editTextPartido.isEnabled = false
            binding.textInputLayoutPartido.error = null
            binding.textInputLayoutPartido.clearFocus()
            binding.editTextDirecciN.setText(selected.domicilio)
            binding.editTextDirecciN.isEnabled = false
            binding.editTextDirecciN.error = null
            binding.textInputLayoutDireccion.clearFocus()
            binding.editTextEmail.setText(selected.email)
            binding.editTextEmail.isEnabled = false
            binding.editTextEmail.error = null
            binding.textInputLayoutEmail.clearFocus()
            binding.editTextTelefono.setText(selected.telefonos)
            binding.editTextTelefono.isEnabled = false
            binding.editTextTelefono.error = null
            binding.textInputLayoutTelefono.clearFocus()

        }
    }

    private fun handlerEdit() {
        binding.editButton.setOnClickListener {
            binding.viewSwitcher.showNext()
            binding.editTextNombre.isEnabled = true
            binding.editTextCuit.isEnabled = true
            binding.editTextPartido.isEnabled = true
            binding.editTextDirecciN.isEnabled = true
            binding.editTextEmail.isEnabled = true
            binding.editTextTelefono.isEnabled = true
            binding.editTextLocalidades.isEnabled = true
        }
    }

    private fun handlerDelete() {
        // ============== BOTON DE BORRAR TORNEO =================
        binding.deleteClubButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro de eliminar el Club? Esta acción será permanente.")
                .setPositiveButton("Borrar Club") { _, _ ->
                    lifecycleScope.launch {
                        viewModel.deleteClub(selected.id)
                        viewModel.deleteTournamentAssociate(selected.id)
                        findNavController().popBackStack(R.id.myClubsFragment, false)
                        Snackbar.make(requireView(),"El Club fue borrado con exito.", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun createClub(): Club {
        val id = selected.id
        val nombre = binding.editTextNombre.text.toString()
        val cuit = binding.editTextCuit.text.toString()
        val provincia = "Buenos Aires"
        val partido = binding.editTextPartido.text.toString()
        val localidad = binding.editTextLocalidades.text.toString()
        val domicilio = binding.editTextDirecciN.text.toString()
        val email = binding.editTextEmail.text.toString()
        val telefono = binding.editTextTelefono.text.toString()

        val club = Club(id,nombre,cuit, provincia,partido,localidad,domicilio,email,telefono, selected.userId)

        return club
    }

    private fun checkCredentials(): Boolean {
        var isValid = true
        val addClubViewModel: AddClubViewModel by viewModels()

        // Validar campo Nombre
        if (!addClubViewModel.checkedNoSpecialCharacters(binding.editTextNombre)) {
            isValid = false
        }

        // Validar campo Cuit
        if (!addClubViewModel.checkedCuit(binding.editTextCuit, selected)) {
            isValid = false
        }

        // Validar campo Partido
        if (!addClubViewModel.checkedPartido(binding.editTextPartido, binding.textInputLayoutPartido)) {
            isValid = false
        }

        //validar localidad

        // Validar campo Direccion
        if (!addClubViewModel.checkedDireccion(binding.editTextDirecciN)) {
            isValid = false
        }

        // Validar campo Email
        if (!addClubViewModel.checkedEmail(binding.editTextEmail, selected)) {
            isValid = false
        }

        // Validar campo Telefono
        if (!addClubViewModel.checkedTelefono(binding.editTextTelefono)) {
            isValid = false
        }

        return isValid
    }
}

