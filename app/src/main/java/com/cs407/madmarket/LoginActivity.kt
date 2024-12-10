package com.cs407.madmarket
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var firebaseAuth: FirebaseAuth
private lateinit var email:EditText
private lateinit var password:EditText
private lateinit var errorText: TextView
private lateinit var loginButton: Button
private lateinit var signupButton: Button
private lateinit var fStore : FirebaseFirestore



class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        email = findViewById(R.id.emailEditText)
        password = findViewById(R.id.passwordEditText)
        errorText = findViewById(R.id.errorTextView)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.SignupButton)

        loginButton.setOnClickListener {
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailPassword(email, password)
            } else {
                errorText.text = "Please enter username and password"
                errorText.visibility = TextView.VISIBLE
            }
        }

        signupButton.setOnClickListener {
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userID = firebaseAuth.currentUser?.uid

                            if (userID != null) {
                                // Create a reference and set user data
                                val userProfile = hashMapOf(
                                    "email" to email,
                                    "username" to "",
                                    "bio" to "",
                                    "password" to password,
                                    "notifications" to true
                                )
                                fStore.collection("users").document(userID)
                                    .set(userProfile)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                                        signInWithEmailPassword(email, password)
                                    }
                                    .addOnFailureListener { errorText.text = "Error storing user data" }
                            }else{
                                errorText.text = "Failed to retrieve user ID"
                                errorText.visibility = TextView.VISIBLE
                            }
                        } else {
                            errorText.text = "Registration Failed: ${task.exception?.message}"
                            errorText.visibility = TextView.VISIBLE
                        }
                    }
            } else {
                errorText.text = "Please enter username and password"
                errorText.visibility = TextView.VISIBLE
            }
        }
    }

    private fun signInWithEmailPassword(username: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    errorText.text = "Sign In Failed: ${task.exception?.message}"
                    errorText.visibility = TextView.VISIBLE
                }
            }
    }
}