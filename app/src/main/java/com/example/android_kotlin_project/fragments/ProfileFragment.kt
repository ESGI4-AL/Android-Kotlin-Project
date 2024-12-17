package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android_kotlin_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    // UI elements
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize EditTexts and Button
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        saveButton = view.findViewById(R.id.saveButton)

        // Fetch user data
        fetchUserData()

        // Save button click listener
        saveButton.setOnClickListener {
            updateUserData()
        }
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "Unknown"
                        val email = document.getString("email") ?: "Unknown"

                        // Set values to EditText
                        nameEditText.setText(name)
                        emailEditText.setText(email)
                    } else {
                        nameEditText.setText("Name not found")
                        emailEditText.setText("Email not found")
                    }
                }
                .addOnFailureListener {
                    nameEditText.setText("Error fetching name")
                    emailEditText.setText("Error fetching email")
                }
        } else {
            nameEditText.setText("No user logged in")
            emailEditText.setText("")
        }
    }

    private fun updateUserData() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val userMap = hashMapOf(
                    "name" to name,
                    "email" to email
                )

                // Update user data in Firestore
                firestore.collection("users").document(userId)
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
