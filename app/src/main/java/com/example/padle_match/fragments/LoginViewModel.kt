package com.example.padle_match.fragments

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


private var auth: FirebaseAuth = Firebase.auth


class LoginViewModel : ViewModel() {

    suspend fun loginUser(email: String, password: String) {
        val request = auth.signInWithEmailAndPassword(email, password)

        try{
            request.addOnSuccessListener() { task ->
            }.await()
        }catch (e: Exception) {
            Log.w("Login Method", "signInWithEmail:failure");
        }
    }

    suspend fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }
}