package com.example.padle_match.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    val db = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth
    suspend fun registerUser(email: String, password: String ): FirebaseUser {
        lateinit var user: FirebaseUser

        try{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.w("REGISTER", "User Created")
                    } else {
                        Log.w("REGISTER", "createUserWithEmail:failure", task.exception)
                    }
                }.await()

        }catch (e: Exception) {
            Log.w("REGISTER", "createUserWithEmail:failure")
        }

        user = auth.currentUser!!;

        Log.w("REGISTER", "User")
        Log.w("REGISTER", user.toString())
        return user;
    }

    suspend fun createUser(usuario: User) {
        try{
            val query = db.collection("users")
            val data = query.add(usuario).await()
            Log.w("Creating User", "The user ${usuario.idUsuario} was created correctily" )
        }catch (e: Exception){
            Log.w("Creating User", "Occurred a error creating user" )
        }
    }


}