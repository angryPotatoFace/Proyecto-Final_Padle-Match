package com.example.padle_match.fragments

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
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
    private lateinit var imageButton2: Button
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private var isEditable = false

    val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_tournament_detail, container, false)
        titulo = v.findViewById(R.id.detail_tituloNombre)
        titulo.isEnabled = false
        detailNombre = v.findViewById( R.id.detail_name)
        detailNombre.isEnabled = false
        detailFecha =  v.findViewById( R.id.detail_date)
        detailFecha.isEnabled = false
        detailCategorias =  v.findViewById( R.id.detail_categorias)
        detailCategorias.isEnabled = false
        detailHorario =  v.findViewById( R.id.detail_hour)
        detailHorario.isEnabled = false
        detailCupos =  v.findViewById( R.id.detail_cupos)
        detailCupos.isEnabled = false
        detailMateriales =  v.findViewById( R.id.detail_materiales)
        detailMateriales.isEnabled = false
        imageButton2 = v.findViewById(R.id.imageButton2)
        return v
    }

    override fun onStart() {
        super.onStart()
        val tournamentSelected : Tournament = TournamentDetailFragmentArgs.fromBundle(requireArguments()).tournamentSelected
        Log.w("Torneo selecionado", tournamentSelected.toString())
        titulo.text = tournamentSelected.titulo
        detailNombre.setText(tournamentSelected.titulo)
        detailFecha.setText(tournamentSelected.fecha)
        detailCategorias.setText((tournamentSelected.categoría))
        detailHorario.setText(tournamentSelected.hora)
        detailCupos.setText(tournamentSelected.cupos.toString())
        detailMateriales.setText("cemento")
        //Snackbar.make(v,tournamentSelected.titulo,Snackbar.LENGTH_SHORT).show()
        var datoAAguardar :String = ""

        imageButton2.setOnClickListener{
            isEditable = true
            detailNombre.isEnabled = true
            detailFecha.isEnabled = true
            detailCategorias.isEnabled = true
            detailHorario.isEnabled = true
            detailCupos.isEnabled = true
            detailMateriales.isEnabled = true

            titulo.text = detailNombre.text.toString()
            val nombreGuardar = detailNombre.text.toString()
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccione una fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()


            detailFecha.setOnClickListener{
                datePicker.show(requireActivity().supportFragmentManager, "tag" )
                datePicker.addOnPositiveButtonClickListener { selection ->
                    val dateString = DateFormat.format("dd/MM/yyyy", Date(selection)).toString()
                    datoAAguardar = dateString
                    detailFecha.setText(dateString)
                }
            }


            val categoriaGuardar = detailCategorias.text.toString()
            val horarioGuardar = detailHorario.text.toString()
            val cuposGuardar = detailCupos.text.toString()
            val materialesGuardar = detailMateriales.text.toString()

            val idTorneo = tournamentSelected.idTorneo

// Buscar el documento del torneo por su ID
            val torneoRef = db.collection("torneos").document(idTorneo)

// Crear un nuevo objeto Torneo con los campos actualizados
            val torneoActualizado = Tournament(
                idTorneo,
                nombreGuardar,
                datoAAguardar,
                horarioGuardar,
                categoriaGuardar,
                cuposGuardar as Number,
                500,
                "Aplausos",
                "Imagen"
            )


// Actualizar los campos del documento en Firestore
            val actualizacion = hashMapOf<String, Any>(
                "titulo" to torneoActualizado.titulo,
                "fecha" to torneoActualizado.fecha,
                "categoría" to torneoActualizado.categoría,
                "hora" to torneoActualizado.hora,
                "cupos" to torneoActualizado.cupos
            )

            torneoRef.update(actualizacion)
                .addOnSuccessListener {
                    Log.d(TAG, "Torneo actualizado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al actualizar el torneo", e)
                }

        }
    }
}