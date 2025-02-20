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
            Log.e("HealthDataRepository", "Error reading step data: ${e.message}", e)
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

            lastSample?.beatsPerMinute.also {
                Log.d("HealthDataRepository", "Last heart rate: $it bpm")
            }
        } catch (e: Exception) {
            Log.e("HealthDataRepository", "Error reading heart rate data: ${e.message}", e)
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

            // format time
            mappedData.forEach { (timeInSeconds, bpm) ->
                val readableTime = formatTime(timeInSeconds.toLong())
                Log.d("HealthDataRepository", "Time: $readableTime, BPM: $bpm")
            }

            mappedData

        } catch (e: Exception) {
            Log.e("HealthDataRepository", "Error reading heart rate data: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Format time in hours and minutes (because heart rate data time is in seconds)
     */
    private fun formatTime(secondsFromStartOfDay: Long): String {
        val hours = secondsFromStartOfDay / 3600
        val minutes = (secondsFromStartOfDay % 3600) / 60
        return String.format("%02d:%02d", hours, minutes)
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

                val userDoc = Tasks.await(firestore.collection("users").document(userId).get())

                if (userDoc.exists()) {
                    Tasks.await(
                        firestore.collection("users").document(userId)
                            .update("bodyComposition", bodyCompositionData)
                    )
                } else {
                    Tasks.await(
                        firestore.collection("users").document(userId)
                            .set(mapOf("bodyComposition" to bodyCompositionData))
                    )
                }
                true
            } catch (e: Exception) {
                Log.e("HealthDataRepository", "Error saving body composition: ${e.message}", e)
                false
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

                if (document != null && document.exists()) {
                    val bodyComposition = document.get("bodyComposition") as? Map<String, Any>
                    if (bodyComposition != null) {
                        val height = bodyComposition["height"] as? String ?: "0"
                        val weight = bodyComposition["weight"] as? String ?: "0"
                        Pair(height, weight)
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("HealthDataRepository", "Error loading body composition: ${e.message}", e)
                null
            }
        }
    }
}
