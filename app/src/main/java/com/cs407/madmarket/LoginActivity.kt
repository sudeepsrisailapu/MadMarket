package com.cs407.madmarket
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth
private lateinit var usernameEditText:EditText
private lateinit var passwordEditText:EditText
private lateinit var errorTextView: TextView
private lateinit var loginButton: Button
private lateinit var signupButton: Button



class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        auth = FirebaseAuth.getInstance()

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        errorTextView = findViewById(R.id.errorTextView)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.SignupButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailPassword(username, password)
            } else {
                errorTextView.text = "Please enter username and password"
                errorTextView.visibility = TextView.VISIBLE
            }
        }

        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Directly register the user with Firebase
                auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful, user created
                            Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                            // Automatically sign in the user
                            signInWithEmailPassword(username, password)  // Automatically log in after registration
                        } else {
                            // Registration failed
                            errorTextView.text = "Registration Failed: ${task.exception?.message}"
                            errorTextView.visibility = TextView.VISIBLE
                        }
                    }
            } else {
                errorTextView.text = "Please enter username and password"
                errorTextView.visibility = TextView.VISIBLE
            }
        }
    }

    private fun signInWithEmailPassword(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in success
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    setContentView(R.layout.activity_main)
                    // Navigate to the main screen or perform post-login actions here
                } else {
                    // If sign-in fails, display a message to the user.
                    errorTextView.text = "Authentication Failed: ${task.exception?.message}"
                    errorTextView.visibility = TextView.VISIBLE
                }
            }
    }
}