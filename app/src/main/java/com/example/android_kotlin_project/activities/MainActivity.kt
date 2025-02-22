package com.example.android_kotlin_project.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.utils.HealthConnectAvailability
import com.example.android_kotlin_project.fragments.PrivacyPolicyFragment
import com.example.android_kotlin_project.repositories.HealthDataRepository
import com.example.android_kotlin_project.repositories.HealthPermissionsRepository
import com.example.android_kotlin_project.viewmodels.HealthViewModel
import com.example.android_kotlin_project.viewmodels.HealthViewModelFactory
import com.example.android_kotlin_project.auth.SignupActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var healthViewModel: HealthViewModel
    private lateinit var permissionStatusTextView: TextView

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            permissionStatusTextView.text = "All permissions granted"
            healthViewModel.fetchDailySteps()
            healthViewModel.fetchLastHeartRate()
            healthViewModel.fetchHeartRateData()
            healthViewModel.fetchOxygenLevel()
        } else {
            permissionStatusTextView.text = "Permissions denied"
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

        // Set up bottom navigation
        val bottomNavigationView  = findViewById<BottomNavigationView>(R.id.navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Initialize Firebase
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

        // Initialize ViewModel
        val healthDataRepository = HealthDataRepository(applicationContext, HealthConnectClient.getOrCreate(applicationContext))
        val permissionsRepository = HealthPermissionsRepository(HealthConnectClient.getOrCreate(applicationContext))

        healthViewModel = ViewModelProvider(this, HealthViewModelFactory(healthDataRepository, permissionsRepository))
            .get(HealthViewModel::class.java)

        // Observe LiveData from ViewModel -> we launch permissions request in main activity and show the status in settings
        healthViewModel.availability.observe(this) { availabilityStatus ->
            when (availabilityStatus) {
                HealthConnectAvailability.INSTALLED -> {
                    healthViewModel.checkAndRequestPermissions(requestPermissionsLauncher)
                }
                HealthConnectAvailability.NOT_SUPPORTED,
                HealthConnectAvailability.NOT_INSTALLED -> {}
            }
        }

        // Start the Health Connect setup process
        healthViewModel.checkHealthConnectAvailability()
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
