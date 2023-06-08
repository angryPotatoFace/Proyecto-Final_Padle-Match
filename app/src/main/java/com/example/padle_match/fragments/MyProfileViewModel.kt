package com.example.padle_match.fragments

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padle_match.entities.Club
import com.example.padle_match.entities.Tournament
import com.example.padle_match.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await



class MyProfileViewModel : ViewModel() {

    val db = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth
    val storage = Firebase.storage(Firebase.app)
    val storageRef = storage.reference
    var kill: MutableLiveData<Boolean> = MutableLiveData(false)


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
        Log.w("USUARIO", user.toString())

        return user;
    }

    suspend fun updateUser(user: User ) {
        val query = db.collection("users")
        val data = query.document(user.idUsuario).set(user)
        data.addOnSuccessListener { document ->
            Log.w("Update Club", "User ${user.idUsuario} was update correctly")
        }.await()
    }


    suspend fun deleteUser(user: User ) {
        val query = db.collection("users")
        val data = query.document(user.idUsuario).delete()
        data.addOnSuccessListener { document ->
            Log.w("Delete User", "User ${user.idUsuario} was deleted correctly")
        }.await()

        val user = Firebase.auth.currentUser
        user!!.delete().await()
    }

    suspend fun updateProfile(profile: User){
        val query = db.collection("users")
        val data = query.document(profile.idUsuario).set(profile)
        data.addOnSuccessListener{ document ->
            Log.w("Update Tournament", "User ${profile.idUsuario} was update correctly")
        }.await()
    }

    suspend fun uploadImagenStorage(data: Uri, udi: String): String {

        var result: String = ""

        // Create the file metadata
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        // Upload file and metadata to the path 'images/mountains.jpg'
        val uploadTask = storageRef.child("images/${udi}/profile/").putFile(data, metadata)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // Request for URL where the image is hosted
            storageRef.child("images/${udi}/profile/").downloadUrl.addOnSuccessListener { uri ->
                result = uri.toString();
            }
        }.await()

        delay(1000);
        return result
    }

}