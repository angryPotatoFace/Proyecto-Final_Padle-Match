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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class AddTournamentFragment: Fragment()  {

    private lateinit var v: View
    private var repository: TournamentRepository = TournamentRepository()
    private lateinit var adapter: TournamentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddTournament: Button

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
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione una fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        var fecha: EditText = v.findViewById(R.id.fechaEditText)

        // ============== CUANDO CLIKEAN EL CAMPO FECHA SE ACTIVA EL DATE PICKER =================
        fecha.setOnClickListener{
            datePicker.show(requireActivity().supportFragmentManager, "tag" )
            datePicker.addOnPositiveButtonClickListener { selection ->
                val dateString = DateFormat.format("dd/MM/yyyy", Date(selection)).toString()
                fecha.setText(dateString)
            }
        }

        // ============== PIDO LOS CLUBS Y LOS AGREGO A LA LISTA =================
       db.collection("clubs")
            .get()
            .addOnSuccessListener { clubs ->
                val data: List<String> = clubs.map { it -> it.data["nombre"] } as List<String>
                val d: Array<String> = data.toTypedArray()
                var clubList:AutoCompleteTextView = v.findViewById(R.id.input_list_clubs)
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
                var categoriaList:AutoCompleteTextView = v.findViewById(R.id.autoCompleteTextView2)
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
                var mateList:AutoCompleteTextView = v.findViewById(R.id.listMateriales)
                (mateList as? MaterialAutoCompleteTextView)?.setSimpleItems(mat as Array<String>)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        // ============== SETEO EL CAMPO DE SELECCION DE IMAGEN =================
        val imagen : EditText = v.findViewById(R.id.ImagenEditText)
        imagen.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // =========== SI SE CARGO LA IMAGEN CORRECTAMENTE SE MUESTRA =================
        val imagen : ImageView = v.findViewById(R.id.imagen)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            imagen.setImageURI(imageUri)
        }
    }

}