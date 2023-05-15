package com.example.padle_match.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


//import com.example.padle_match.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TournamentDetailFragment : Fragment() {

    private lateinit var v: View
    private lateinit var titulo: TextView
    private lateinit var detailNombre: EditText
    private lateinit var detailFecha: EditText
    private lateinit var detailCategorias: EditText
    private lateinit var detailHorario: EditText
    private lateinit var detailCupos: EditText
    private lateinit var detailMateriales: EditText
    private lateinit var detailImagen : EditText
    private lateinit var deleteButton : Button
    private lateinit var saveButton : Button
    private lateinit var cancelButton : Button
    private lateinit var editButton: Button
    private lateinit var viewSwitcher : ViewSwitcher

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_tournament_detail, container, false)
        titulo = v.findViewById(R.id.detail_tituloNombre)
        detailNombre = v.findViewById( R.id.detail_name)
        detailFecha =  v.findViewById( R.id.detail_date)
        detailCategorias =  v.findViewById( R.id.detail_categorias)
        detailHorario =  v.findViewById( R.id.detail_hour)
        detailCupos =  v.findViewById( R.id.detail_cupos)
        detailMateriales =  v.findViewById( R.id.detail_materiales)
        detailImagen = v.findViewById(R.id.ImagenEditTextDetail)
        deleteButton = v.findViewById(R.id.deleteTournamentButton)
        saveButton = v.findViewById(R.id.saveTournamentButton)
        cancelButton = v.findViewById(R.id.cancelTournamentButton)
        editButton = v.findViewById(R.id.editTournamentButton)
        viewSwitcher = v.findViewById(R.id.viewSwitcherTournament)
        return v
    }

    override fun onStart() {
        super.onStart()
        val tournamentSelected : Tournament = TournamentDetailFragmentArgs.fromBundle(requireArguments()).tournamentSelected
        Log.w("Torneo selecionado", tournamentSelected.toString())
        titulo.setText(tournamentSelected.titulo)
        detailNombre.setText(tournamentSelected.titulo)
        detailFecha.setText(tournamentSelected.fecha)
        detailCategorias.setText((tournamentSelected.categoría))
        detailHorario.setText(tournamentSelected.hora)
        detailCupos.setText(tournamentSelected.cupos.toString())
        detailMateriales.setText("cemento")

        // ============== CREO OBJETO DATE PICKER =================
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione una fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()


        // ============== CUANDO CLIKEAN EL CAMPO FECHA SE ACTIVA EL DATE PICKER =================
        detailFecha.setOnClickListener{
            datePicker.show(requireActivity().supportFragmentManager, "tag" )
            datePicker.addOnPositiveButtonClickListener { selection ->
                val dateString = DateFormat.format("dd/MM/yyyy", Date(selection)).toString()
                detailFecha.setText(dateString)
            }
        }

        // ============== CREO OBJETO TIME PICKER =================
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Seleccione una hora")
                .build()


        // ============== CUANDO CLIKEAN EL CAMPO HORA SE ACTIVA EL TIME PICKER =================
        detailHorario.setOnClickListener{
            timePicker.show(requireActivity().supportFragmentManager, "tag")
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                detailHorario.setText(String.format("%02d:%02d", hour, minute))
            }
        }

        // ============== PIDO LOS MATERIALES DE CANCHA Y LOS AGREGO A LA LISTA =================
        db.collection("materialDeCancha")
            .get()
            .addOnSuccessListener { materiales ->
                val data: List<String> = materiales.map { it -> it.data["materiales"] }[0] as List<String>
                val mat: Array<String> = data.toTypedArray()
                var mateList:AutoCompleteTextView = v.findViewById(R.id.detail_materiales)
                (mateList as? MaterialAutoCompleteTextView)?.setSimpleItems(mat as Array<String>)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


        // ============== SETEO EL CAMPO DE SELECCION DE IMAGEN =================
        val imagen : EditText = v.findViewById(R.id.ImagenEditTextDetail)
        imagen.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // ============== BOTON DE EDITAR TORNEO =================
        editButton.setOnClickListener {
            viewSwitcher.showNext()
            detailNombre.isEnabled = true
            detailFecha.isEnabled = true
            detailCategorias.isEnabled = true
            detailHorario.isEnabled = true
            detailCupos.isEnabled = true
            detailMateriales.isEnabled = true
            detailImagen.isEnabled = true
        }

        // ============== BOTON DE CANCELAR CAMBIOS =================
        cancelButton.setOnClickListener {
            viewSwitcher.showPrevious()
            detailNombre.setText(tournamentSelected.titulo)
            detailNombre.isEnabled = false
            detailFecha.setText(tournamentSelected.fecha)
            detailFecha.isEnabled = false
            detailCategorias.setText(tournamentSelected.categoría)
            detailCategorias.isEnabled = false
            detailHorario.setText(tournamentSelected.hora)
            detailHorario.isEnabled = false
            detailCupos.setText(tournamentSelected.cupos.toString())
            detailCupos.isEnabled = false
            detailMateriales.setText("cemento")
            detailMateriales.isEnabled = false
            detailImagen.setText(tournamentSelected.imagenTorneo)
            detailImagen.isEnabled = false
        }

        // ============== BOTON DE GUARDAR CAMBIOS TORNEO =================
        saveButton.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Confirmar cambios?")
                .setPositiveButton("SI") { _, _ ->
                    // aca guardas los cambios
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }

        // ============== BOTON DE BORRAR TORNEO =================
        deleteButton.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro que desea borrar el torneo? Esta acción no se puede deshacer.")
                .setPositiveButton("Borrar torneo") { _, _ ->
                    // aca borras
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // =========== SI SE CARGO LA IMAGEN CORRECTAMENTE SE MUESTRA =================
        val imagen : ImageView = v.findViewById(R.id.imagenDetailTorneo)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imagen.setImageURI(imageUri)
        }
    }


}