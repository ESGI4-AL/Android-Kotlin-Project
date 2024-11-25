package com.example.android_kotlin_project.viewmodels

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_project.repositories.HealthDataRepository
import com.example.android_kotlin_project.repositories.HealthPermissionsRepository
import com.example.android_kotlin_project.utils.HealthConnectAvailability
import kotlinx.coroutines.launch

class HealthViewModel(
    private val healthDataRepository: HealthDataRepository,
    private val permissionsRepository: HealthPermissionsRepository
) : ViewModel() {

    // Expose availability as LiveData from the repository
    val availability: LiveData<HealthConnectAvailability> = healthDataRepository.availability

    // LiveData to handle permission related messages
    private val _permissionStatus = MutableLiveData<String>()
    val permissionStatus: LiveData<String> get() = _permissionStatus

    private val _dailySteps = MutableLiveData<String>()
    val dailySteps: LiveData<String> get() = _dailySteps

    private val _lastHeartRate = MutableLiveData<String>()
    val lastHeartRate: LiveData<String> get() = _lastHeartRate

    private val _heartRateData = MutableLiveData<List<Pair<Float, Float>>>()
    val heartRateData: LiveData<List<Pair<Float, Float>>> = _heartRateData

    private val _oxygenLevel = MutableLiveData<String>()
    val oxygenLevel: LiveData<String> get() = _oxygenLevel

    /**
     * Check Health Connect availability
     */
    fun checkHealthConnectAvailability() {
        viewModelScope.launch {
            try {
                healthDataRepository.checkAvailability()
            } catch (e: Exception) {
                _permissionStatus.value = "Error checking Health Connect availability: ${e.message}"
            }
        }
    }

    /**
     * Check permissions and request if necessary
     */
    fun checkAndRequestPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        viewModelScope.launch {
            try {
                if (!permissionsRepository.hasAllPermissions()) {
                    permissionsRepository.requestPermissions(launcher)
                } else {
                    _permissionStatus.value = "Permissions already granted"
                    fetchDailySteps()
                    fetchLastHeartRate()
                    fetchHeartRateData()
                    fetchOxygenLevel()
                }
            } catch (e: Exception) {
                _permissionStatus.value = "Error checking permissions: ${e.message}"
            }
        }
    }

    /**
     * Fetch daily steps data
     */
    fun fetchDailySteps() {
        viewModelScope.launch {
            try {
                val stepRecords = healthDataRepository.getDailySteps()
                Log.d("HealthConnect", "Step Records: $stepRecords")

                // If no records are found
                if (stepRecords.isEmpty()) {
                    _dailySteps.value = """
                    No steps data available today
                    Please check:
                    1. Health Connect is installed
                    2. Health Connect has permissions
                    3. The device has step data for today
                """.trimIndent()
                } else {
                    val totalSteps = stepRecords.sumOf { it.count }

                    _dailySteps.value = """
                    $totalSteps
                """.trimIndent()
                }
            } catch (e: Exception) {
                _dailySteps.value = "Error loading step data: ${e.message}"
                Log.e("HealthConnect", "Error fetching daily steps", e)
            }
        }
    }

    /**
     * Fetch the last heart rate data
     */
    fun fetchLastHeartRate() {
        viewModelScope.launch {
            try {
                val lastHeartRate = healthDataRepository.getLastHeartRate()
                _lastHeartRate.value = lastHeartRate?.toString() ?: "0"
            } catch (e: Exception) {
                _lastHeartRate.value = "Error loading heart rate data: ${e.message}"
            }
        }
    }

    /**
     * Fetch heart rate data for the day
     */
    fun fetchHeartRateData() {
        viewModelScope.launch {
            _heartRateData.value = healthDataRepository.getHeartRateData()
        }
    }

    fun fetchOxygenLevel() {
        viewModelScope.launch {
            try {
                val oxygenLevel = healthDataRepository.getOxygenLevel()
                _oxygenLevel.value = oxygenLevel.toString() ?: "0"
            } catch (e: Exception) {
                _oxygenLevel.value = "Error loading heart rate data: ${e.message}"
            }
        }
    }
}
