package com.example.android_kotlin_project.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val signupButton: Button = findViewById(R.id.submit_signup)
        signupButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.name_input).text.toString()
            val email = findViewById<EditText>(R.id.email_input).text.toString()
            val password = findViewById<EditText>(R.id.password_input).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.confirm_password_input).text.toString()

            // Initialize error text views
            val nameErrorTextView: TextView = findViewById(R.id.name_error)
            val emailErrorTextView: TextView = findViewById(R.id.email_error)
            val passwordErrorTextView: TextView = findViewById(R.id.password_error)
            val confirmPasswordErrorTextView: TextView = findViewById(R.id.confirm_password_error)

            var isValid = true

            // Clear previous errors
            nameErrorTextView.visibility = TextView.GONE
            emailErrorTextView.visibility = TextView.GONE
            passwordErrorTextView.visibility = TextView.GONE
            confirmPasswordErrorTextView.visibility = TextView.GONE

            // Check for empty fields
            if (name.trim().isEmpty()) {
                nameErrorTextView.text = getString(R.string.name_error)
                nameErrorTextView.visibility = TextView.VISIBLE
                isValid = false
            }

            if (email.trim().isEmpty()) {
                emailErrorTextView.text = getString(R.string.email_error)
                emailErrorTextView.visibility = TextView.VISIBLE
                isValid = false
            }

            if (password.trim().isEmpty()) {
                passwordErrorTextView.text = getString(R.string.password_errror)
                passwordErrorTextView.visibility = TextView.VISIBLE
                isValid = false
            }

            if (confirmPassword.trim().isEmpty()) {
                confirmPasswordErrorTextView.text = getString(R.string.confirm_password_error)
                confirmPasswordErrorTextView.visibility = TextView.VISIBLE
                isValid = false
            }

            // Check if password and confirm password match
            if (password != confirmPassword) {
                confirmPasswordErrorTextView.text = getString(R.string.passwords_matching_error)
                confirmPasswordErrorTextView.visibility = TextView.VISIBLE
                isValid = false
            }

            // Proceed if all fields are valid
            if (isValid) {
                registerUser(email, password, name)
            }
        }

        // Navigate to LoginActivity
        val signUpLink: TextView = findViewById(R.id.textView_login)
        signUpLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Reset error messages when typing in the fields
        resetErrorOnTextChange(R.id.name_input, R.id.name_error)
        resetErrorOnTextChange(R.id.email_input, R.id.email_error)
        resetErrorOnTextChange(R.id.password_input, R.id.password_error)
        resetErrorOnTextChange(R.id.confirm_password_input, R.id.confirm_password_error)
    }

    private fun resetErrorOnTextChange(editTextId: Int, errorTextViewId: Int) {
        val editText: EditText = findViewById(editTextId)
        val errorTextView: TextView = findViewById(errorTextViewId)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                // Hide the error if the user starts typing
                if (!editable.isNullOrEmpty()) {
                    errorTextView.visibility = TextView.GONE
                }
            }
        })
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserData(user?.uid, email, name)
                } else {
                    val emailErrorTextView: TextView = findViewById(R.id.email_error)
                    emailErrorTextView.text = task.exception?.message
                    emailErrorTextView.visibility = TextView.VISIBLE
                }
            }
    }

    private fun saveUserData(userId: String?, email: String, name: String) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "email" to email,
            "name" to name
        )

        userId?.let {
            db.collection("users").document(it)
                .set(user)
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(baseContext, "Erreur lors de la sauvegarde : ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
