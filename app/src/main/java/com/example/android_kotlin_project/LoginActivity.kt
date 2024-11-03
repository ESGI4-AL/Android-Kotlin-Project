package com.example.android_kotlin_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  val firestore= FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Find the button by its ID and set an onClickListener to navigate to SignUpActivity
        val signUpButton: Button = findViewById(R.id.button_signUp)
        signUpButton.setOnClickListener {
            // Create an intent to navigate to SignUpActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


        // Find the icon by its ID and set an onClickListener to navigate to home
        val backButton: ImageView = findViewById(R.id.imageView_leftArrow)
        backButton.setOnClickListener {
            // Create an intent to navigate to home
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        
        val emailEditText: TextInputEditText = findViewById(R.id.textInputEditText_email)
        val passwordEditText: TextInputEditText = findViewById(R.id.textInputEditView_password)
        val loginButton: Button = findViewById(R.id.button_login)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                checkIfEmailExistsInFirestore(email, password)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkIfEmailExistsInFirestore(email: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No user found with this email
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                } else {
                    // User found, now attempt login
                    loginUser(email, password)
                }
            }
            .addOnFailureListener {
                // Handle query failure
                Toast.makeText(this, "Error checking user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { signInTask ->
                if (signInTask.isSuccessful) {
                    // Login successful
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val exception = signInTask.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        // Wrong password
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                    } else {
                        // General login failure
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}