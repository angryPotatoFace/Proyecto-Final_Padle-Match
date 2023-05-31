package com.example.padle_match.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.padle_match.R
import com.example.padle_match.databinding.FragmentMyProfileBinding
import com.example.padle_match.entities.User
import kotlinx.coroutines.launch


class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var viewModel: MyProfileViewModel
    private lateinit var imageUri: Uri
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
            binding.detailName.setText( currUser.nombre )
            binding.detailSurname.setText( currUser.apellido )
            binding.detailEmail.setText( currUser.email )
            binding.detailPhone.setText( currUser.telefono)
            binding.detailDni.setText( currUser.dni )
        }

        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

    }

    override fun onStart() {
        super.onStart()
        binding.editButton.setOnClickListener{
            enableFields()
        }

        binding.saveButton.setOnClickListener {
            val user = createUser()
            lifecycleScope.launch {
                viewModel.updateUser(user)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imagen = binding.profileImage
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            if( data.data !== null ) {
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
    }

    fun createUser(): User {

        val id = currUser.idUsuario
        var nombre= binding.detailName.text.toString()
        var apellido = binding.detailSurname.text.toString()
        var email= binding.detailEmail.text.toString()
        var telefono= binding.detailPhone.text.toString()
        var dni= binding.detailDni.text.toString()
        var img = currUser.imgProfile

        val user = User(id,nombre,apellido,email,telefono,dni,img)
        return user
    }


}