package com.example.padle_match.fragments


import android.app.Activity
import android.app.AlertDialog
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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.padle_match.R
import com.example.padle_match.entities.Tournament
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*


//import com.example.padle_match.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TournamentDetailFragment : Fragment()  {

    private lateinit var v: View
    private lateinit var titulo: TextView
    private lateinit var detailNombre: EditText
    private lateinit var detailFecha: EditText
    private lateinit var detailCategorias: EditText
    private lateinit var detailHorario: EditText
    private lateinit var detailCupos: EditText
    private lateinit var detailMateriales: EditText
    private lateinit var detailCostoInscripcion: EditText
    private lateinit var detailClub: EditText
    private lateinit var detailPremio: EditText
    private lateinit var detailImagen: EditText
    private lateinit var imageDisplay : ImageView
    private lateinit var deleteButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var editButton: Button
    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var viewModel: TournamentDetailFragmentViewModel
    private  var imageUri: Uri = Uri.EMPTY
    private lateinit var tournamentSelec  : Tournament
    private var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_tournament_detail, container, false)

        titulo = v.findViewById(R.id.detail_tituloNombre)

        detailNombre = v.findViewById(R.id.detail_name)

        detailFecha = v.findViewById(R.id.detail_date)

        detailClub = v.findViewById(R.id.detail_club)

        detailCategorias = v.findViewById(R.id.detail_categorias)

        detailHorario = v.findViewById(R.id.detail_hour)

        detailCupos = v.findViewById(R.id.detail_cupos)

        detailMateriales = v.findViewById(R.id.detail_materiales)

        detailCostoInscripcion = v.findViewById(R.id.detail_costoInscripcion)

        detailPremio = v.findViewById(R.id.detail_premio)

        detailImagen = v.findViewById(R.id.ImagenEditTextDetail)

        imageDisplay = v.findViewById(R.id.imagenDetailTorneo)

        blockFields()

        deleteButton = v.findViewById(R.id.deleteProfile)
        saveButton = v.findViewById(R.id.saveButton)
        cancelButton = v.findViewById(R.id.cancelButton)
        editButton = v.findViewById(R.id.editButton)
        viewSwitcher = v.findViewById(R.id.viewSwitcher)
        return v
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TournamentDetailFragmentViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        val tournamentSelected: Tournament =
            TournamentDetailFragmentArgs.fromBundle(requireArguments()).tournamentSelected

        setValues( tournamentSelected )

        val datePicker = viewModel.createDatePicker();
        datePickerHandler(datePicker, detailFecha);

        val timePicker = viewModel.createHourPicker()
        hourPickerHandler(timePicker, detailHorario)


        lifecycleScope.launch {

            var data = viewModel.getClubsList()
            ( detailClub as? MaterialAutoCompleteTextView)?.setSimpleItems(data);

            var data_cat = viewModel.getCategoriasList()
            ( detailCategorias as? MaterialAutoCompleteTextView)?.setSimpleItems(data_cat)

            var data_material = viewModel.getMaterialesList();
            ( detailMateriales as? MaterialAutoCompleteTextView)?.setSimpleItems(data_material)
        }

        detailImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        handlerEdit()

        handlerCancel( tournamentSelected )

        handlerSave();

        handlerDelete()

        }

    private fun setValues(tournamentSelected: Tournament) {
        Log.w("Torneo selecionado", tournamentSelected.toString())
        tournamentSelec = tournamentSelected
        titulo.text = tournamentSelected.titulo
        detailNombre.setText(tournamentSelected.titulo)
        detailFecha.setText(tournamentSelected.fecha)
        detailCategorias.setText(tournamentSelected.categoría)
        detailHorario.setText(tournamentSelected.hora)
        detailCupos.setText(tournamentSelected.cupos.toString())
        detailCostoInscripcion.setText(tournamentSelected.costoInscripción.toString())
        detailPremio.setText(tournamentSelected.premios)
        detailMateriales.setText(tournamentSelected.materialCancha)
        Glide.with(this).load(tournamentSelected.imagenTorneo).into(imageDisplay)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // =========== SI SE CARGO LA IMAGEN CORRECTAMENTE SE MUESTRA =================
        val imagen = imageDisplay
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            if( data.data !== null ) {
                imagen.setImageURI(data.data)
                imageUri = data.data!!
            }
            /*  ======================================================  */
        }
    }
    private fun handlerDelete() {
        // ============== BOTON DE BORRAR TORNEO =================
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro de eliminar el torneo? Esta acción será permanente.")
                .setPositiveButton("Borrar torneo") { _, _ ->
                    lifecycleScope.launch {
                        val torneo = createTournament()
                        viewModel.deleteTournament( torneo.id )
                        findNavController().popBackStack(R.id.myTournamentsFragment, false)
                        Snackbar.make(requireView(),"El torneo fue modificado con exito", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }



    private fun handlerSave() {
        // ============== BOTON DE GUARDAR CAMBIOS TORNEO =================
        saveButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro de aplicar los cambios realizados?")
                .setPositiveButton("SI") { _, _ ->

                    lifecycleScope.launch {
                        val torneo = createTournament()
                        if( imageUri != Uri.EMPTY ) {
                            val url = viewModel.uploadImagenStorage( imageUri, torneo.id );
                            torneo.imagenTorneo = url
                        }
                        viewModel.updateTournament(torneo, torneo.id)
                        findNavController().popBackStack(R.id.myTournamentsFragment, false)
                        Snackbar.make(requireView(),"El torneo fue modificado con exito", Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun handlerCancel(tournamentSelected: Tournament) {
        // ============== BOTON DE CANCELAR CAMBIOS =================
        cancelButton.setOnClickListener {
            viewSwitcher.showPrevious()
            detailNombre.setText(tournamentSelected.titulo)
            detailNombre.isEnabled = false
            detailFecha.setText(tournamentSelected.fecha)
            detailFecha.isEnabled = false
            detailCategorias.setText(tournamentSelected.categoría.toString())
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
    }

    private fun handlerEdit() {
        // ============== BOTON DE EDITAR TORNEO =================
        editButton.setOnClickListener {
            viewSwitcher.showNext()
            detailNombre.isEnabled = true
            detailClub.isEnabled = true;
            detailFecha.isEnabled = true
            detailCategorias.isEnabled = true
            detailHorario.isEnabled = true
            detailCupos.isEnabled = true
            detailMateriales.isEnabled = true
            detailCostoInscripcion.isEnabled = true
            detailPremio.isEnabled = true
            detailImagen.isEnabled = true
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

    private fun hourPickerHandler(timePicker: MaterialTimePicker, item: EditText ) {
        item.setOnClickListener {
            timePicker.show(requireActivity().supportFragmentManager, "tag")
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                item.setText(String.format("%02d:%02d", hour, minute))
            }
        }
    }

    private fun blockFields() {
        titulo.isEnabled = false
        detailNombre.isEnabled = false
        detailFecha.isEnabled = false
        detailCategorias.isEnabled = false
        detailHorario.isEnabled = false
        detailCupos.isEnabled = false
    }

    private fun createTournament(): Tournament {
        val id = tournamentSelec.id
        val nombre = detailNombre.text.toString();
        val club = detailClub.text.toString();
        val date = detailFecha.text.toString();
        val hour = detailHorario.text.toString();
        val category = detailCategorias.text.toString();
        val material = detailMateriales.text.toString();
        val cupo = detailCupos.text.toString().toInt()
        val cost = detailCupos.text.toString().toInt();
        val premio = detailPremio.text.toString();
        val userId = tournamentSelec.userId
        val imagen = tournamentSelec.imagenTorneo
        var idClub = tournamentSelec.idClub

        lifecycleScope.launch {
            idClub = viewModel.getIdClubByName(club)
        }

        val retorno = Tournament(id, nombre, date, hour, category, material, cupo,  cost, premio, imagen, userId, idClub,"TODO","TODO")

        return retorno
    }

}
