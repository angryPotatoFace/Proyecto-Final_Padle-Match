package com.example.padle_match.fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
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
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.adapter.TournamentAdapter
import com.example.padle_match.entities.TournamentRepository
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTournamentFragment: Fragment()  {

    private lateinit var v: View
    private var repository: TournamentRepository = TournamentRepository()
    private lateinit var adapter: TournamentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTournament: Button
    private lateinit var editName: EditText
    private lateinit var editClub: AutoCompleteTextView
    private lateinit var editFecha: EditText
    private lateinit var editHorario: EditText
    private lateinit var editCategorias: AutoCompleteTextView
    private lateinit var editMateriales: AutoCompleteTextView
    private lateinit var editCupo: EditText
    private lateinit var editCosto: EditText
    private lateinit var editPremios: EditText
    private lateinit var editImagen : EditText
    private lateinit var addBtn : Button

    val db = Firebase.firestore

    companion object {
        fun newInstance() = AddTournamentFragment()
    }

    private lateinit var viewModel: AddTournamentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_add_tournament, container, false)
        editName = v.findViewById(R.id.editTextAddTournament_name)
        editClub = v.findViewById(R.id.editTextAddTournament_club)
        editFecha = v.findViewById(R.id.editTextAddTournament_fecha)
        editHorario = v.findViewById(R.id.editTextAddTournament_horario)
        editCategorias = v.findViewById(R.id.editTextAddTournament_categorias)
        editMateriales = v.findViewById(R.id.editTextAddTournament_materiales)
        editCupo = v.findViewById(R.id.editTextAddTournament_cupo)
        editCosto = v.findViewById(R.id.editTextAddTournament_costo)
        editPremios = v.findViewById(R.id.editTextAddTournament_premios)
        editImagen = v.findViewById(R.id.editTextAddTournament_imagen)
        addBtn = v.findViewById(R.id.addTournamentButton)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTournamentViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onStart() {
        super.onStart()

        // ============== CREO OBJETO DATE PICKER =================
        // Permite solo seleccionar una fecha a partir del dia actual
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione una fecha")
                .setSelection(today)
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.from(today))
                        .build()
                )
                .build()

        // ============== CUANDO CLIKEAN EL CAMPO FECHA SE ACTIVA EL DATE PICKER =================
        editFecha.setOnClickListener{
            datePicker.show(requireActivity().supportFragmentManager, "tag" )
            datePicker.addOnPositiveButtonClickListener { selection ->
                val dateString = DateFormat.format("dd/MM/yyyy", Date(selection)).toString()
                editFecha.setText(dateString)
            }
        }

        // ============== CREO OBJETO TIME PICKER =================
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Seleccione una hora")
                .build()

        // ============== CUANDO CLIKEAN EL CAMPO HORA SE ACTIVA EL TIME PICKER =================
        editHorario.setOnClickListener {
            timePicker.show(requireActivity().supportFragmentManager, "tag")
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                editHorario.setText(String.format("%02d:%02d", hour, minute))
            }
        }

        // ============== PIDO LOS CLUBS Y LOS AGREGO A LA LISTA =================
       db.collection("clubs")
            .get()
            .addOnSuccessListener { clubs ->
                val data: List<String> = clubs.map { it -> it.data["nombre"] } as List<String>
                val d: Array<String> = data.toTypedArray()
                var clubList:AutoCompleteTextView = editClub
                (clubList as? MaterialAutoCompleteTextView)?.setSimpleItems(d)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        // ============== PIDO LAS CATEGORIAS Y LOS AGREGO A LA LISTA =================
        db.collection("categorias")
            .get()
            .addOnSuccessListener { categorias ->
                val data: List<String> = categorias.map { it -> it.data["nombreCategoria"] }[0] as List<String>
                val categorias: Array<String> = data.toTypedArray()
                Log.w("CATEGORIAS", categorias.toString())
                var categoriaList:AutoCompleteTextView = editCategorias
                (categoriaList as? MaterialAutoCompleteTextView)?.setSimpleItems(categorias as Array<String>)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        // ============== PIDO LOS MATERIALES DE CANCHA Y LOS AGREGO A LA LISTA =================
        db.collection("materialDeCancha")
            .get()
            .addOnSuccessListener { materiales ->
                val data: List<String> = materiales.map { it -> it.data["materiales"] }[0] as List<String>
                val mat: Array<String> = data.toTypedArray()
                var mateList:AutoCompleteTextView = editMateriales
                (mateList as? MaterialAutoCompleteTextView)?.setSimpleItems(mat as Array<String>)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        // ============== SETEO EL CAMPO DE SELECCION DE IMAGEN =================
        editImagen.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }
        
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // =========== SI SE CARGO LA IMAGEN CORRECTAMENTE SE MUESTRA =================
        val imagen : ImageView = v.findViewById(R.id.AddTournamentimagen)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            imagen.setImageURI(imageUri)
        }
    }

}