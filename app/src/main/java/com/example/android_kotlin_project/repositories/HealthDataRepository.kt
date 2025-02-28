package com.example.android_kotlin_project.repositories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.lifecycle.MutableLiveData
import com.example.android_kotlin_project.utils.HealthConnectAvailability
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId

class HealthDataRepository(private val context: Context, private val healthConnectClient: HealthConnectClient) {

    var availability = MutableLiveData<HealthConnectAvailability>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
            emptyList()
        }
    }

    /**
     * Get the last heart rate data
     */
    suspend fun getLastHeartRate(): Long? {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = today.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        return try {
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val response = healthConnectClient.readRecords(request)

            val allSamples = response.records.flatMap { record ->
                record.samples
            }

            val lastSample = allSamples.maxByOrNull { it.time }

            lastSample?.beatsPerMinute
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get heart rate data for the day
     */
    suspend fun getHeartRateData(): List<Pair<Float, Float>> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = today.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        return try {
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val response = healthConnectClient.readRecords(request)

            val mappedData = response.records.flatMap { record ->
                record.samples.map { sample ->
                    val timeInSeconds = Duration.between(startOfDay, sample.time).seconds.toFloat()
                    Pair(timeInSeconds, sample.beatsPerMinute.toFloat())
                }
            }

            mappedData

        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get oxygen level data for the day
     */
    suspend fun getOxygenLevel(): String? {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = today.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        return try {
            val request = ReadRecordsRequest(
                recordType = OxygenSaturationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val response = healthConnectClient.readRecords(request)

            if (response.records.isEmpty()) "0" else response.records.last().percentage.toString()
        } catch (e: Exception) {
            "0"
        }
    }

    /**
     * Save body composition data to Firebase
     */
    suspend fun saveBodyComposition(height: String, weight: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: return@withContext false

                val bodyCompositionData = hashMapOf(
                    "height" to height,
                    "weight" to weight,
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                val userDocRef = firestore.collection("users").document(userId)
                val userDoc = Tasks.await(userDocRef.get())

                if (userDoc.exists()) {
                    Tasks.await(userDocRef.update("bodyComposition", bodyCompositionData))
                } else {
                    Tasks.await(userDocRef.set(mapOf("bodyComposition" to bodyCompositionData)))
                }

                val verifyDoc = Tasks.await(userDocRef.get())
                val savedData = verifyDoc.get("bodyComposition") as? Map<String, Any>
                return@withContext savedData != null
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    /**
     * Load body composition data from Firebase
     */
    suspend fun loadBodyComposition(): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: return@withContext null

                val document = Tasks.await(
                    firestore.collection("users").document(userId).get()
                )

                if (document.exists()) {
                    val bodyComposition = document.get("bodyComposition") as? Map<String, Any>
                    if (bodyComposition != null) {
                        val height = bodyComposition["height"] as? String ?: "0"
                        val weight = bodyComposition["weight"] as? String ?: "0"
                        return@withContext Pair(height, weight)
                    } else {
                        return@withContext null
                    }
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }
}
