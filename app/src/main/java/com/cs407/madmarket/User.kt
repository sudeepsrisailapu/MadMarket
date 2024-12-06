package com.cs407.madmarket

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class User : AppCompatActivity (){
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var notificationsSwitch: Switch
    private lateinit var saveButton: Button
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        usernameEditText = findViewById(R.id.usernameLabel)
        bioEditText = findViewById(R.id.bioLabel)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
        saveButton = findViewById(R.id.saveButton)
        errorTextView = findViewById(R.id.errorTextView)

        Log.d("UserProfile", "saveButton: $saveButton")

        val user = firebaseAuth.currentUser

        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username")
                    val bio = document.getString("bio")
                    val notifications = document.getBoolean("notifications") ?: true

                    usernameEditText.setText(username)
                    bioEditText.setText(bio)
                    notificationsSwitch.isChecked = notifications
                }
            }.addOnFailureListener{ e ->
                errorTextView.text = "Error gathering data: ${e.message}"
            }
            saveButton.setOnClickListener {
                Log.d("UserProfile", "Save button clicked")
                saveUserProfile(user.uid)
            }
        }
    }

    private fun saveUserProfile(userId: String) {
        val updatedUser = usernameEditText.text.toString()
        val updatedBio = bioEditText.text.toString()
        val notificationsEnabled = notificationsSwitch.isChecked

        val userData = hashMapOf(
            "username" to updatedUser,
            "bio" to updatedBio,
            "notifications" to notificationsEnabled
        )

        val userRef = firestore.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                userRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        errorTextView.text = "Error saving data: ${e.message}"
                    }
            } else {
                errorTextView.text = "User document does not exist!"
            }
        }
    }
}