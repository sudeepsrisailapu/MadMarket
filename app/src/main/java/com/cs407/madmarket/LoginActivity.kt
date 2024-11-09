package com.cs407.madmarket
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var firebaseAuth: FirebaseAuth
private lateinit var username:EditText
private lateinit var password:EditText
private lateinit var errorText: TextView
private lateinit var loginButton: Button
private lateinit var signupButton: Button



class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        firebaseAuth = FirebaseAuth.getInstance()

        username = findViewById(R.id.usernameEditText)
        password = findViewById(R.id.passwordEditText)
        errorText = findViewById(R.id.errorTextView)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.SignupButton)

        loginButton.setOnClickListener {
            val username = username.text.toString().trim()
            val password = password.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailPassword(username, password)
            } else {
                errorText.text = "Please enter username and password"
                errorText.visibility = TextView.VISIBLE
            }
        }

        signupButton.setOnClickListener {
            val username = username.text.toString().trim()
            val password = password.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                            signInWithEmailPassword(username, password)
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