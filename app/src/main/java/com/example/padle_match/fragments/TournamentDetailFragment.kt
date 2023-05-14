package com.example.padle_match.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.padle_match.R
import com.example.padle_match.entities.Tournament
import com.google.android.material.snackbar.Snackbar


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


        //Snackbar.make(v,tournamentSelected.titulo,Snackbar.LENGTH_SHORT).show()
    }
}