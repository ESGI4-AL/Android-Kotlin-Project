package com.example.android_kotlin_project.repositories

import android.health.connect.HealthPermissions
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient

class HealthPermissionsRepository(private val healthConnectClient: HealthConnectClient) {

    private val requiredPermissions = setOf(
        HealthPermissions.READ_STEPS,
        HealthPermissions.READ_HEART_RATE,
        HealthPermissions.READ_OXYGEN_SATURATION
    )

    /**
     * Check if all required permissions are granted
     */
    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(requiredPermissions)
    }

    /**
     * Request permissions from the user
     */
    fun requestPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(requiredPermissions.toTypedArray())
    }
}
