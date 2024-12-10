package com.cs407.madmarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var emailTextView: TextView
    private lateinit var usernameEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var bioEditText : EditText
    private lateinit var currentPasswordEditText : EditText
    private lateinit var saveButton : Button
    private lateinit var userID: String
    private lateinit var documentReference: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        // Find the TextView for email
        emailTextView = view.findViewById(R.id.emailTextView)
        usernameEditText = view.findViewById(R.id.usernameEditText)
        bioEditText = view.findViewById(R.id.bioEditText)
        saveButton = view.findViewById(R.id.saveButton)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText)

        // Fetch user data
        fetchUserData()

        saveButton.setOnClickListener { updateUserData() }

        return view
    }

    private fun fetchUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            userID = firebaseAuth.currentUser?.uid.toString()
            documentReference = fStore.collection("users").document(userID)

            // Add a listener to fetch user data
            documentReference.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Error fetching user data: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val email = documentSnapshot.getString("email")
                    val username = documentSnapshot.getString("username")
                    val bio = documentSnapshot.getString("bio")
                    val password = documentSnapshot.getString("password")

                    emailTextView.text = email ?: "Email not found"
                    usernameEditText.setText(username ?: "Enter username")
                    bioEditText.setText(bio ?: "Enter bio")
                    passwordEditText.setText(password ?: "Enter password")

                } else {
                    emailTextView.text = "User data not available"
                    Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            emailTextView.text = "No user logged in"
            Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserData(){
        val newUser = usernameEditText.text.toString().trim()
        val newBio = bioEditText.text.toString().trim()
        val newPassword = passwordEditText.text.toString().trim()
        val currentPassword = currentPasswordEditText.text.toString().trim()

        if (currentPassword.isEmpty()) {
            Toast.makeText(context, "Please enter your current password", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userID = currentUser.uid
            val userDocRef = fStore.collection("users").document(userID)

            if (newUser.isNotEmpty()) {
                userDocRef.update("username", newUser)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Username updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Failed to update username: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            if (newBio.isNotEmpty()) {
                userDocRef.update("bio", newBio)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Bio updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Failed to update bio: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            if (newPassword.isNotEmpty()) {
                val email = currentUser.email
                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword) // Replace "currentPassword" with the user's actual current password
                    currentUser.reauthenticate(credential)
                        .addOnSuccessListener {
                            currentUser.updatePassword(newPassword)
                            userDocRef.update("password", newPassword)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                    passwordEditText.text.clear()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to update password: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Re-authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "User email is unavailable for re-authentication", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }
}