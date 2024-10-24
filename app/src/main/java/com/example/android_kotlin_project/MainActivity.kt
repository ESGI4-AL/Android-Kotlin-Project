package com.example.android_kotlin_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private fun hideBottomNavigationLabels(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size()) {
            val item = bottomNavigationView.menu.getItem(i)
            item.setTitle("")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView  = findViewById<BottomNavigationView>(R.id.navigation)
        hideBottomNavigationLabels(bottomNavigationView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}