package com.example.padle_match.fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.databinding.FragmentAddTournamentBinding
import com.example.padle_match.entities.Tournament
import com.example.padle_match.entities.TournamentRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.util.*



class AddTournamentFragment: Fragment()  {

    private lateinit var binding: FragmentAddTournamentBinding
    lateinit var imageUri: Uri

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

        var fecha: EditText = binding.fechaEditText
        val datePicker = viewModel.createDatePicker();

        datePickerHandler(datePicker, fecha);

        lifecycleScope.launch {

            var clubList = binding.inputListClubs
            var data = viewModel.getClubsList()
            (clubList as? MaterialAutoCompleteTextView)?.setSimpleItems(data);



            var categoriaList =binding.autoCompleteTextView2
            var data_cat = viewModel.getCategoriasList()
            ( categoriaList as? MaterialAutoCompleteTextView)?.setSimpleItems(data_cat as Array<String>)


            var materialList = binding.listMateriales
            var data_material = viewModel.getMaterialesList();
            (materialList as? MaterialAutoCompleteTextView)?.setSimpleItems(data_material as Array<String>)

        }

        val imagen = binding.ImagenEditText
        imagen.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }


        handlerAddTournament( binding.btnAgregarTorneo )
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // =========== SI SE CARGO LA IMAGEN CORRECTAMENTE SE MUESTRA =================
        val imagen = binding.imagen
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


    private fun handlerAddTournament(btn: AppCompatButton) {
        btn.setOnClickListener {
             val torneo = createTournament();

            lifecycleScope.launch {
                var udi = viewModel.addTournament(torneo)
                val url = viewModel.uploadImagenStorage( imageUri, udi )
                torneo.imagenTorneo = url;
                viewModel.updateTournament(torneo, udi);
            }
        }
    }

    private fun createTournament(): Tournament {

        val nombre = binding.inputName.text.toString();
        val club = binding.inputListClubs.text.toString();
        val date = binding.fechaEditText.text.toString();
        val hour = binding.horarioEditText.text.toString();
        val category = binding.autoCompleteTextView2.text.toString();
        val material = binding.listMateriales.text.toString();
        val cupo = binding.cupoEditText.text.toString().toInt()
        val cost = binding.inputCostoInscripcion.text.toString().toInt();
        val premio = binding.inputCostoInscripcion.text.toString();

        val retorno =Tournament(
            nombre,
            club,
            date,
            hour,
            category,
            material,
            cupo,
            cost,
            premio,
            "loading..."
        )

        return retorno
    }




}