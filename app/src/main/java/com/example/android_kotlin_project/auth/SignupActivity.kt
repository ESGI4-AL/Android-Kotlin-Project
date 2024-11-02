package com.example.android_kotlin_project.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.android_kotlin_project.R
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
            if(name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty() || confirmPassword.trim().isEmpty() ){
                Toast.makeText(this, "Veuiilez renseigner tout les champs", Toast.LENGTH_SHORT).show()
            }else{
                if (password == confirmPassword) {
                    registerUser(email, password, name)
                } else {
                    Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                }
            }

        }

      // Find the text by its ID and set an onClickListener to navigate to LoginActivity
        val signUpLink: TextView =findViewById(R.id.textView_login)
        signUpLink.setOnClickListener{
            // Create an intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserData(user?.uid, email, name)
                } else {
                    Toast.makeText(baseContext, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(baseContext, "Inscription réussie !", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(baseContext, "Erreur lors de la sauvegarde : ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


}