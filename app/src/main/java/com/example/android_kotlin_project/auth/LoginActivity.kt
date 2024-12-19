package com.example.android_kotlin_project.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.activities.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailErrorTextView: TextView
    private lateinit var passwordErrorTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Setup UI components
        emailEditText = findViewById(R.id.textInputEditText_email)
        passwordEditText = findViewById(R.id.textInputEditView_password)
        loginButton = findViewById(R.id.button_login)

        // Error TextViews
        emailErrorTextView = findViewById(R.id.email_error)
        passwordErrorTextView = findViewById(R.id.password_error)

        // Navigate to SignUpActivity
        registerLink = findViewById(R.id.register_Link)
        registerLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Clear previous error messages
            clearErrorMessages()

            // Check if both fields are filled
            if (email.isNotEmpty() && password.isNotEmpty()) {
                checkIfEmailExistsInFirestore(email, password)
            } else {
                if (email.isEmpty()) {
                    emailErrorTextView.text = getString(R.string.email_error)
                    emailErrorTextView.visibility = TextView.VISIBLE
                }
                if (password.isEmpty()) {
                    passwordErrorTextView.text = getString(R.string.password_errror)
                    passwordErrorTextView.visibility = TextView.VISIBLE
                }
            }
        }
    }

    private fun checkIfEmailExistsInFirestore(email: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    emailErrorTextView.text = getString(R.string.user_not_found_error)
                    emailErrorTextView.visibility = TextView.VISIBLE
                } else {
                    // User found => now attempt login
                    loginUser(email, password)
                }
            }
            .addOnFailureListener {
                emailErrorTextView.text = getString(R.string.error_checking_user)
                emailErrorTextView.visibility = TextView.VISIBLE
            }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { signInTask ->
                if (signInTask.isSuccessful) {
                    // Login successful, navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Prevent user from navigating back to LoginActivity
                } else {
                    val exception = signInTask.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        passwordErrorTextView.text = getString(R.string.incorrect_password)
                        passwordErrorTextView.visibility = TextView.VISIBLE
                    } else {
                        emailErrorTextView.text = getString(R.string.authentication_failed)
                        emailErrorTextView.visibility = TextView.VISIBLE
                    }
                }
            }
    }

    private fun clearErrorMessages() {
        emailErrorTextView.visibility = TextView.GONE
        passwordErrorTextView.visibility = TextView.GONE
    }
}
