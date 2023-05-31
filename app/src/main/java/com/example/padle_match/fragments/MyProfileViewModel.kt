package com.example.padle_match.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

val db = Firebase.firestore
private var auth: FirebaseAuth = Firebase.auth

class MyProfileViewModel : ViewModel() {

    suspend fun getUser(): User {

        var uid = auth.currentUser!!.uid
        lateinit var user: User
        val query =  db.collection("users").whereEqualTo("idUsuario", uid)
        val usuario = query.get().await();

        val data = usuario.documents.get(0).data

        Log.w("USUARIO", data.toString())
        val id = data!!.get("idUsuario") as String
        var nombre= data["nombre"] as String
        var apellido = data["apellido"] as String
        var email= data["email"] as String
        var telefono= data["telefono"] as String
        var dni= data["dni"] as String
        var img = data["imgProfile"] as? String?: "No image"

        user = User(id,nombre,apellido,email,telefono,dni,img)

        return user;
    }

    suspend fun updateUser(user: User ) {
        val query = db.collection("users")
        val data = query.document(user.idUsuario).set(user)
        data.addOnSuccessListener { document ->
            Log.w("Update Club", "User ${user.idUsuario} was update correctly")
        }.await()
    }

}