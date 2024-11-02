package com.example.android_kotlin_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
    }







}