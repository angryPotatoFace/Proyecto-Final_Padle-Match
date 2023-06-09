package com.example.padle_match.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.padle_match.MainActivity
import com.example.padle_match.NoBottomNavActivity
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentMyProfileBinding
import com.example.padle_match.entities.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File


class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var viewModel: MyProfileViewModel
    private var imageUri: Uri = Uri.EMPTY
    private lateinit var currUser: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)

        lifecycleScope.launch {
            currUser = viewModel.getUser()

            setFields(currUser)
            handlerCancel(currUser)
            handlerDelete(currUser)
            handlerSave()
            handlerEdit()
            handlerImage()
        }

        binding.closeSessionButton.setOnClickListener() {
            closeSession()
        }
    }

    private fun handlerImage() {
        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }
    }

    private fun handlerCancel(currUser: User) {
        binding.cancelButton.setOnClickListener {
            binding.viewSwitcher.showPrevious()
            binding.detailName.setText(currUser.nombre)
            binding.detailSurname.setText(currUser.apellido)
            binding.detailPhone.setText(currUser.telefono)
            binding.detailDni.setText(currUser.dni)
            blockFields()
            disableError()
        }
    }

    private suspend fun checkCredentials(): Boolean {
        var isValid = true
        val registerViewModel : RegisterViewModel by viewModels()

        // Validar campo Nombre
        if (!registerViewModel.checkedNoSpecialCharacters(binding.detailName)) {
            isValid = false
        }

        // Validar campo Apellido
        if (!registerViewModel.checkedNoSpecialCharacters(binding.detailSurname)) {
            isValid = false
        }

        // Validar campo Telefono
        if (!registerViewModel.checkedTelefono(binding.detailPhone)) {
            isValid = false
        }

        // Validar campo DNI
        if (!registerViewModel.checkedDNI(binding.detailDni, binding.detailDni.text.toString())) {
            isValid = false
        }

        return isValid
    }

    private fun handlerSave() {
        binding.saveButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("¿Está seguro de aplicar los cambios realizados?")
                .setPositiveButton("SI") { _, _ ->
                    lifecycleScope.launch {
                        if(checkCredentials()){
                            val user = createUser()
                            viewModel.updateUser(user)
                            if( imageUri != Uri.EMPTY) {
                                Log.w("IMAGEN", "ENTROOOOOOO")
                                val url = viewModel.uploadImagenStorage( imageUri, user.idUsuario );
                                user.imgProfile = url
                            }
                            viewModel.updateProfile(user);
                            Snackbar.make(binding.root,"Los datos fueron guardados correctamente", Snackbar.LENGTH_LONG).show()
                            binding.viewSwitcher.showPrevious()
                            blockFields()
                        }
                    }
                }
                .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun handlerDelete(user: User) {

            binding.deleteProfile.setOnClickListener {
                lifecycleScope.launch {
                val result = viewModel.areClubsAvaible()
                    if(result) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("¿Está seguro de eliminar el perfil? Esta acción será permanente.")
                            .setPositiveButton("Borrar Perfil") { _, _ ->
                                lifecycleScope.launch {
                                    viewModel.deleteUser(user)
                                    val prefs = requireActivity().getSharedPreferences(
                                        "prefs",
                                        Context.MODE_PRIVATE
                                    )
                                    with(prefs.edit()) {
                                        putBoolean("clear_credentials", true)
                                        apply()
                                    }
                                    closeSession()
                                    findNavController().popBackStack(R.id.loginFragment, false)
                                    Snackbar.make(
                                        binding.root,
                                        "Su usuario fue borrado correctamente",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                }
                            }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                dialog.dismiss()
                            }
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "No se puede borrar el perfil porque hay clubes vigentes",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }


    private fun handlerEdit() {
        binding.editButton.setOnClickListener{
            binding.viewSwitcher.showNext()
            enableFields()
        }
    }

    private fun setFields( user: User) {
        binding.detailName.setText( user.nombre )
        binding.detailSurname.setText( user.apellido )
        binding.detailEmail.setText( user.email )
        binding.detailPhone.setText( user.telefono)
        binding.detailDni.setText( user.dni )

        if( user.imgProfile != "No image" && user.imgProfile != "User created" ){
            Glide.with(this).load(user.imgProfile).into(binding.profileImage)
        }else {
            Glide.with(this).load(R.drawable.logo_img_base).into(binding.profileImage)
        }
        binding.profileImage.isEnabled = false
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagen = binding.profileImage
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                imagen.setImageURI(data.data)
                imageUri = data.data!!
            }
        }
    }

    fun enableFields() {
        binding.detailName.isEnabled = true
        binding.detailSurname.isEnabled = true
        binding.detailPhone.isEnabled = true
        binding.detailDni.isEnabled = true
        binding.profileImage.isEnabled = true
    }

    fun blockFields(){
        binding.detailName.isEnabled = false
        binding.detailSurname.isEnabled = false
        binding.detailPhone.isEnabled = false
        binding.detailDni.isEnabled = false
        binding.profileImage.isEnabled = false
    }

    fun disableError(){
        binding.detailName.error = null
        binding.detailSurname.error = null
        binding.detailPhone.error = null
        binding.detailDni.error = null
    }

    fun createUser(): User {
        val id = currUser.idUsuario
        var nombre= binding.detailName.text.toString()
        var apellido = binding.detailSurname.text.toString()
        var email= binding.detailEmail.text.toString()
        val telef = binding.detailPhone.text.toString()
        val telefono = asignarTelefono(telef)
        var dni= binding.detailDni.text.toString()
        var img = currUser.imgProfile

        val user = User(id,nombre,apellido,email,telefono,dni,img)
        return user
    }

    private fun asignarTelefono(telef: String): String {
        val telefono: String

        if (telef.startsWith("549")) {
            telefono = telef
        } else {
            telefono = "549$telef"
        }

        return telefono
    }

    fun closeSession() {
        val intent = Intent(activity, NoBottomNavActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}