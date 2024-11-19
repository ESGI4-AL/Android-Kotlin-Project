package com.example.android_kotlin_project.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android_kotlin_project.repositories.HealthDataRepository
import com.example.android_kotlin_project.repositories.HealthPermissionsRepository

class HealthViewModelFactory(
    private val healthDataRepository: HealthDataRepository,
    private val permissionsRepository: HealthPermissionsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(healthDataRepository, permissionsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
