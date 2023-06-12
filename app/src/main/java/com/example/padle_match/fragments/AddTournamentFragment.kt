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
import com.example.padle_match.databinding.FragmentAddTournamentBinding
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*



class AddTournamentFragment: Fragment()  {

    private lateinit var binding: FragmentAddTournamentBinding
    lateinit var imageUri: Uri
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var lista: Array<String>
    private lateinit var listaIds: List<String>

    companion object {
        fun newInstance() = AddTournamentFragment()
    }

    private lateinit var viewModel: AddTournamentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTournamentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTournamentViewModel::class.java)
    }


    override fun onStart() {
        super.onStart()
        
        var fecha: EditText = binding.editTextAddTournamentFecha

        val datePicker = viewModel.createDatePicker();

        var hora : EditText = binding.editTextAddTournamentHorario
        val timePicker = viewModel.createTimePicker();

        datePickerHandler(datePicker, fecha);
        timePickerHandler(timePicker, hora)

        lifecycleScope.launch {

            var clubList = binding.editTextAddTournamentClub
            var data = viewModel.getClubsList()
            lista = data;

            if( data.isEmpty() ){
                data = arrayOf("No hay clubs dados de altas")
            }
            (clubList as? MaterialAutoCompleteTextView)?.setSimpleItems(data)

            listaIds = viewModel.getClubsIds();

            var categoriaList =binding.editTextAddTournamentCategorias
            var data_cat = viewModel.getCategoriasList()
            ( categoriaList as? MaterialAutoCompleteTextView)?.setSimpleItems(data_cat as Array<String>)

            var materialList = binding.editTextAddTournamentMateriales
            var data_material = viewModel.getMaterialesList();
            (materialList as? MaterialAutoCompleteTextView)?.setSimpleItems(data_material as Array<String>)

        }

        val imagen = binding.editTextAddTournamentImagen
        imagen.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }


        handlerAddTournament( binding.btnAgregarTorneo )
        handlerImOrganizator()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // =========== SI SE CARGO LA IMAGEN CORRECTAMENTE SE MUESTRA =================
        val imagen = binding.AddTournamentImagen
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if( data.data !== null ) {
                imagen.setImageURI(data.data)
                imageUri = data.data!!
            }
        /*  ======================================================  */
        }
    }

    private fun datePickerHandler(datePicker: MaterialDatePicker<Long>, item: EditText ) {
        item.setOnClickListener{
            datePicker.show(requireActivity().supportFragmentManager, "tag" )
            datePicker.addOnPositiveButtonClickListener { selection ->
                val dateString = DateFormat.format("dd/MM/yyyy", Date(selection)).toString()
                item.setText(dateString)
            }
        }
    }

    private fun timePickerHandler(timePicker: MaterialTimePicker, item: EditText){
        item.setOnClickListener {
            timePicker.show(requireActivity().supportFragmentManager, "tag")
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                binding.editTextAddTournamentHorario.setText(String.format("%02d:%02d", hour, minute))
            }
        }
    }


    private fun handlerAddTournament(btn: AppCompatButton) {
        btn.setOnClickListener {
            if(checkCredentials()) {
                val torneo = createTournament();
                lifecycleScope.launch {
                    var udi = viewModel.addTournament(torneo)
                    val url = viewModel.uploadImagenStorage(imageUri, udi)
                    torneo.imagenTorneo = url;
                    torneo.id = udi;
                    viewModel.updateTournament(torneo, udi);

                    findNavController().popBackStack(R.id.myTournamentsFragment, false)
                    Snackbar.make(
                        requireView(),
                        "El torneo fue agregado con exito",
                        Snackbar.LENGTH_LONG
                    ).show()
                    cleanInputs()
                }
            }
        }
    }

    private fun checkCredentials(): Boolean {
        var isValid = true

        // campos opcionales: Material de cancha, cupo, costo, premios

        // Validar campo Nombre
        if (!viewModel.checkedNoSpecialCharacters(binding.editTextAddTournamentName)) {
            isValid = false
        }

        // Validar campo Club
        if(!viewModel.checkedClub(binding.editTextAddTournamentClub, binding.inputAddClub)){
            isValid = false
        }

        // Validar campo Fecha
        if(!viewModel.checkedRequired(binding.editTextAddTournamentFecha, binding.inputAddDate)){
            isValid = false
        }

        // Validar campo Horario
        if(!viewModel.checkedRequired(binding.editTextAddTournamentHorario, binding.inputAddHour)){
            isValid = false
        }

        // Validar campo categoria
        if(!viewModel.checkedRequired(binding.editTextAddTournamentCategorias, binding.inputAddCategories)){
            isValid = false
        }

        // Validar campo nombre coordinador
        if (!viewModel.checkedNoSpecialCharacters(binding.nombreCoordinador)) {
            isValid = false
        }

        // Validar campo telefono coordinador
        if(!viewModel.checkedTelefono(binding.telefonoCoordinador)){
            isValid = false
        }


        return isValid
    }

    private fun handlerImOrganizator() {
        binding.checkBox2.setOnClickListener() {
            if (binding.checkBox2.isChecked) {
                lifecycleScope.launch {
                    val user = viewModel.getUser()
                    binding.nombreCoordinador.setText(user.nombre)
                    binding.telefonoCoordinador.setText(user.telefono)
                }
            }else{
                binding.nombreCoordinador.setText("")
                binding.telefonoCoordinador.setText("")
            }
        }
    }

    private fun cleanInputs() {
        binding.editTextAddTournamentName.setText("")
        binding.editTextAddTournamentClub.setText("")
        binding.editTextAddTournamentFecha.setText("")
        binding.editTextAddTournamentHorario.setText("")
        binding.editTextAddTournamentCategorias.setText("")
        binding.editTextAddTournamentMateriales.setText("")
        binding.editTextAddTournamentCupo.setText("")
        binding.editTextAddTournamentCosto.setText("")
        binding.editTextAddTournamentPremios.setText("")
        binding.AddTournamentImagen.setImageURI(Uri.EMPTY)
    }

    private fun createTournament(): Tournament {
        val item = binding.editTextAddTournamentClub.text.toString();
        val i =  getItemImpl( lista, item );

        val nombre = binding.editTextAddTournamentName.text.toString();
        val club = binding.editTextAddTournamentClub.text.toString();
        val date = binding.editTextAddTournamentFecha.text.toString();
        val hour = binding.editTextAddTournamentHorario.text.toString();
        val category = binding.editTextAddTournamentCategorias.text.toString();
        val material = binding.editTextAddTournamentMateriales.text.toString();
        val cupo = binding.editTextAddTournamentCupo.text.toString().toInt()
        val cost = binding.editTextAddTournamentCosto.text.toString().toInt();
        val premio = binding.editTextAddTournamentPremios.text.toString();
        val userId = auth.currentUser!!.uid
        var idClub = listaIds[i]
        var nombreCoor = binding.nombreCoordinador.text.toString()
        var telefonoCood = binding.telefonoCoordinador.text.toString()

        lifecycleScope.launch {
            idClub = viewModel.getIdClubByName("nombre")
        }

        val retorno = Tournament("", nombre, date, hour, category, material, cupo,  cost, premio, "loading...", userId, idClub, nombreCoor, telefonoCood)


        return retorno
    }

    private fun <T> getItemImpl(list: Array<String>, item: T): Int {
        list.forEachIndexed { index, it ->
            if (it == item)
                return index
        }
        return -1
    }

}