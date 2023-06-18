package com.example.padle_match.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
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
    private var imageUri = Uri.EMPTY
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
                handlerNoClubs()
            } else {
                (clubList as? MaterialAutoCompleteTextView)?.setSimpleItems(data)
            }

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

    private fun handlerNoClubs() {
        binding.editTextAddTournamentClub.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("No hay clubes dados de alta. Por favor ingrese un nuevo club desde 'mis clubes' para continuar")
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagen = binding.AddTournamentImagen
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if( data.data !== null ) {
                imagen.setImageURI(data.data)
                imageUri = data.data!!
            }
        }
    }

    private fun datePickerHandler(datePicker: MaterialDatePicker<Long>, item: EditText) {
        item.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener { selection ->
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = Date(selection)
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val nuevaFecha = calendar.time
                val nuevaFechaString = formatoFecha.format(nuevaFecha)
                item.setText(nuevaFechaString)
                Log.d("DatePickerHandler", "Selected date: $nuevaFechaString")
            }
        }
    }

    private fun timePickerHandler(timePicker: MaterialTimePicker, item: EditText){
        item.setOnClickListener {
            timePicker.show(requireActivity().supportFragmentManager, "tag")
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                item.setText(String.format("%02d:%02d", hour, minute))
            }
        }
    }


    private fun handlerAddTournament(btn: AppCompatButton) {
        btn.setOnClickListener {
            if( checkCredentials().all { !it } ) {
                lifecycleScope.launch {
                    val torneo = createTournament();
                    cleanInputs()
                    Log.d(tag, "torneo")
                    var udi = viewModel.addTournament(torneo)
                    if( imageUri != Uri.EMPTY) {
                        val url = viewModel.uploadImagenStorage(imageUri, udi)
                        torneo.imagenTorneo = url;
                    }
                    torneo.id = udi;
                    viewModel.updateTournament(torneo, udi);
                    findNavController().popBackStack(R.id.myTournamentsFragment, false)
                    Snackbar.make( requireView(), "El torneo fue agregado con exito", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(requireView(), "Hay campos invalidos", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun checkCredentials(): List<Boolean> {
        var isValid = arrayListOf<Boolean>()

        // campos opcionales: Material de cancha, cupo, costo, premios

        // Validar campo Nombre
        isValid.add( !viewModel.checkedRequired(binding.editTextAddTournamentName, binding.textInputLayout) )
        isValid.add( !viewModel.checkedNoSpecialCharacters(binding.editTextAddTournamentName) )

        // Validar campo Club
        isValid.add( !viewModel.checkedClub(binding.editTextAddTournamentClub, binding.inputAddClub) )


        // Validar campo Fecha
        isValid.add( !viewModel.checkedRequired(binding.editTextAddTournamentFecha, binding.inputAddDate) )

        // Validar campo Horario
        isValid.add( !viewModel.checkedRequired(binding.editTextAddTournamentHorario, binding.inputAddHour) )

        // Validar campo categoria
        isValid.add( !viewModel.checkedRequired(binding.editTextAddTournamentCategorias, binding.inputAddCategories) )

        // Validar campo nombre coordinador
        isValid.add( !viewModel.checkedRequired(binding.nombreCoordinador, binding.textInputLayoutNombreCoordinador) )
        isValid.add( !viewModel.checkedNoSpecialCharacters(binding.nombreCoordinador) )


        // Validar campo telefono coordinador
        isValid.add( !viewModel.checkedRequired(binding.telefonoCoordinador, binding.textInputLayoutTelefonoCoordinador) )
        isValid.add( !viewModel.checkedTelefono(binding.telefonoCoordinador) )

        Log.d("Validación", "Contenido del array: $isValid")

        return isValid
    }

    private fun  handlerImOrganizator() {
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

    private suspend fun createTournament(): Tournament {

        lateinit var retorno: Tournament
        val item = binding.editTextAddTournamentClub.text.toString();
        val i =  getItemImpl( lista, item );

        val nombre = binding.editTextAddTournamentName.text.toString();
        val club = binding.editTextAddTournamentClub.text.toString();
        val date = binding.editTextAddTournamentFecha.text.toString();
        val hour = binding.editTextAddTournamentHorario.text.toString();
        val category = binding.editTextAddTournamentCategorias.text.toString();
        val material = binding.editTextAddTournamentMateriales.text.toString();
        var cupo = 0
        if ( !binding.editTextAddTournamentCupo.text.toString().isEmpty() ){
            cupo = binding.editTextAddTournamentCupo.text.toString().toInt()
        }
        var cost = 0
        if ( !binding.editTextAddTournamentCosto.text.toString().isEmpty() ){
            cost = binding.editTextAddTournamentCosto.text.toString().toInt()
        }

        val premio = binding.editTextAddTournamentPremios.text.toString();
        val userId = auth.currentUser!!.uid
        var idClub = listaIds[i]
        var nombreCoor = binding.nombreCoordinador.text.toString()
        var telefonoCood = "549" + binding.telefonoCoordinador.text.toString()

        idClub = viewModel.getIdClubByName(club)
        retorno = Tournament("", nombre, date, hour, category, material, cupo,  cost, premio, "loading...", userId, idClub, nombreCoor, telefonoCood)

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