package com.example.android_kotlin_project.repositories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.MutableLiveData
import com.example.android_kotlin_project.utils.HealthConnectAvailability
import java.time.LocalDate
import java.time.ZoneId

class HealthDataRepository(private val context: Context, private val healthConnectClient: HealthConnectClient) {

    var availability = MutableLiveData<HealthConnectAvailability>()

    init {
        checkAvailability()
    }

    /**
     * Check if health connect is available on the device
     */
    fun checkAvailability() {
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        availability.value = when (availabilityStatus) {
            HealthConnectClient.SDK_UNAVAILABLE -> HealthConnectAvailability.NOT_SUPPORTED
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                redirectToPlayStoreForHealthConnect()
                HealthConnectAvailability.NOT_INSTALLED
            }
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }
    }

    /**
     * Redirect to Play Store for Health Connect installation or update
     */
    private fun redirectToPlayStoreForHealthConnect() {
        val uriString = "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setPackage("com.android.vending")
                data = Uri.parse(uriString)
                putExtra("overlay", true)
                putExtra("callerId", context.packageName)
            }
        )
    }

    /**
     * Get daily steps data
     */
    suspend fun getDailySteps(): List<StepsRecord> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = today.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        return try {
            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val response = healthConnectClient.readRecords(request)
            response.records
        } catch (e: Exception) {
            Log.e("HealthDataRepository", "Error reading step data: ${e.message}", e)
            emptyList()
        }
    }
}
