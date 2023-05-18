package com.example.padle_match.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ViewSwitcher
import com.example.padle_match.R

class ClubDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ClubDetailFragment()
    }

    private lateinit var viewModel: ClubDetailViewModel
    lateinit var v : View
    lateinit var editTextNombre : EditText
    lateinit var editTextCuit : EditText
    lateinit var editTextProvincia: EditText
    lateinit var editTextPartido: EditText
    lateinit var editTextDireccion: EditText
    lateinit var editTextEmail: EditText
    lateinit var editTextTelefono : EditText
    lateinit var editButton: Button
    lateinit var saveButton: Button
    lateinit var cancelButton: Button
    lateinit var deleteButton : Button
    lateinit var viewSwitcher: ViewSwitcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_club_detail, container, false)
        editTextNombre = v.findViewById(R.id.editTextNombre)
        editTextCuit = v.findViewById(R.id.editTextCuit)
        editTextProvincia = v.findViewById(R.id.editTextProvincia)
        editTextPartido = v.findViewById(R.id.editTextPartido)
        editTextDireccion = v.findViewById(R.id.editTextDirección)
        editTextEmail = v.findViewById(R.id.editTextEmail)
        editTextTelefono = v.findViewById(R.id.editTextTelefono)
        editButton = v.findViewById(R.id.editButton)
        saveButton = v.findViewById(R.id.saveButton)
        cancelButton = v.findViewById(R.id.cancelButton)
        deleteButton = v.findViewById(R.id.deleteClubButton)
        viewSwitcher = v.findViewById(R.id.viewSwitcher)
        editTextNombre.setText("Chacarita")
        editTextCuit.setText("24272897626")
        editTextProvincia.setText("Buenos Aires")
        editTextPartido.setText("San Isidro")
        editTextDireccion.setText("Teodoro Garcia 3550")
        editTextEmail.setText("chacaritafc@gmail.com")
        editTextTelefono.setText("+54 9 3755 27-5457")
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ClubDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        editButton.setOnClickListener {
            viewSwitcher.showNext()
            editTextNombre.isEnabled = true
            editTextCuit.isEnabled = true
            editTextProvincia.isEnabled = true
            editTextPartido.isEnabled = true
            editTextDireccion.isEnabled = true
            editTextEmail.isEnabled = true
            editTextTelefono.isEnabled = true
        }
        saveButton.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro de aplicar los cambios realizados?")
                .setPositiveButton("SI") { _, _ ->
                    // aca guardas los cambios
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }

        deleteButton.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro que desea borrar el club? Esta acción será permanente.")
                .setPositiveButton("Borrar club") { _, _ ->
                    // aca borras
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

}