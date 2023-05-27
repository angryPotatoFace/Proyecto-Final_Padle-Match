package com.example.padle_match.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MyProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val db = Firebase.firestore
    val currentUser = FirebaseAuth.getInstance().currentUser

    suspend fun updateProfile(user: User, id: String) {
        val query = db.collection("users")
        val data = query.document(id).set(user)
        data.addOnSuccessListener { document ->
            Log.w("Update Profile", "Profile ${id} was update correctly")
        }.await()
    }

    suspend fun deleteProfile(id: String) {
        val query = db.collection("users")
        val data = query.document(id).delete()
        data.addOnSuccessListener { document ->
            Log.w("Deleted Profile", "Profile  ${id} was deleted correctly")
        }.await()
    }

    suspend fun getUser(): User? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore
        val usersCollection = db.collection("users")

        var usuario: User? = null  // Variable to hold the result

        currentUser?.uid?.let { userId ->
            val documentSnapshot = usersCollection.document(userId).get().await()
            if (documentSnapshot.exists()) {
                // The document exists, you can get the user data here
                usuario = documentSnapshot.toObject(User::class.java)
            } else {
                // The document doesn't exist
                usuario = null
            }
        }

        return usuario  // Return the user object outside the lambda expression
    }


}
