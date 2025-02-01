package com.example.android_kotlin_project.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.android_kotlin_project.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView  = findViewById<BottomNavigationView>(R.id.navigation)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        FirebaseApp.initializeApp(this)

        val settingsIcon = findViewById<View>(R.id.settings_icon)
        val settingsPopup = findViewById<View>(R.id.settings_popup)
        val settingsPopupText = findViewById<View>(R.id.settings_popup_text)

        settingsIcon.setOnClickListener {
            settingsPopup.visibility = if (settingsPopup.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        settingsPopupText.setOnClickListener {
            settingsPopup.visibility = View.GONE
            navController.navigate(R.id.settingsFragment)
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
