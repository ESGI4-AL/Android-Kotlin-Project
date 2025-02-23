package com.example.android_kotlin_project.viewmodels

import android.app.Application
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.repositories.HealthDataRepository
import com.example.android_kotlin_project.repositories.HealthPermissionsRepository
import com.example.android_kotlin_project.utils.HealthConnectAvailability
import com.example.android_kotlin_project.utils.HealthSaveStatus
import kotlinx.coroutines.launch

class HealthViewModel(
    application: Application,
    private val healthDataRepository: HealthDataRepository,
    private val permissionsRepository: HealthPermissionsRepository
) : AndroidViewModel(application) {

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

    private val _height = MutableLiveData("0")
    val height: LiveData<String> get() = _height

    private val _weight = MutableLiveData("0")
    val weight: LiveData<String> get() = _weight

    private val _saveStatus = MutableLiveData(HealthSaveStatus.IDLE)
    val saveStatus: LiveData<HealthSaveStatus> get() = _saveStatus

    /**
     * Check Health Connect availability
     */
    fun checkHealthConnectAvailability() {
        viewModelScope.launch {
            try {
                healthDataRepository.checkAvailability()
            } catch (e: Exception) {
                _permissionStatus.value = getApplication<Application>().getString(R.string.error_checking_availability, e.message)
            }
        }
    }

    /**
     * Check permissions and request if necessary
     */
    fun checkAndRequestPermissions(launcher: ActivityResultLauncher<Array<String>>, context: Context) {
        viewModelScope.launch {
            try {
                if (!permissionsRepository.hasAllPermissions()) {
                    permissionsRepository.requestPermissions(launcher)
                } else {
                    _permissionStatus.value = context.getString(R.string.all_permissions_granted)
                    fetchDailySteps()
                    fetchLastHeartRate()
                    fetchHeartRateData()
                    fetchOxygenLevel()
                }
            } catch (e: Exception) {
                _permissionStatus.value = context.getString(R.string.error_checking_permissions, e.message)
            }
        }
    }

    /**
     * Update permission status
     */
    fun updatePermissionStatus(status: String) {
        _permissionStatus.value = status
    }

    /**
     * Fetch daily steps data
     */
    fun fetchDailySteps() {
        viewModelScope.launch {
            try {
                val stepRecords = healthDataRepository.getDailySteps()

                // If no records are found
                if (stepRecords.isEmpty()) {
                    _dailySteps.value = getApplication<Application>().getString(R.string.no_steps_data)
                } else {
                    val totalSteps = stepRecords.sumOf { it.count }

                    _dailySteps.value = """
                    $totalSteps
                """.trimIndent()
                }
            } catch (e: Exception) {
                _dailySteps.value = getApplication<Application>().getString(R.string.error_loading_data, e.message)
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
                _dailySteps.value = getApplication<Application>().getString(R.string.error_loading_data, e.message)
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
                _oxygenLevel.value = oxygenLevel.toString()
            } catch (e: Exception) {
                _oxygenLevel.value = getApplication<Application>().getString(R.string.error_loading_data, e.message)
            }
        }
    }

    /**
     * Save body composition data
     */
    fun saveBodyComposition(height: String, weight: String) {
        viewModelScope.launch {
            try {
                _saveStatus.value = HealthSaveStatus.SAVING
                val result = healthDataRepository.saveBodyComposition(height, weight)
                if (result) {
                    _height.value = height
                    _weight.value = weight
                    _saveStatus.value = HealthSaveStatus.SUCCESS
                } else {
                    _saveStatus.value = HealthSaveStatus.ERROR
                }
            } catch (e: Exception) {
                _saveStatus.value = HealthSaveStatus.ERROR
            }
        }
    }

    /**
     * Load body composition data
     */
    fun loadBodyComposition() {
        viewModelScope.launch {
            val data = healthDataRepository.loadBodyComposition()
            if (data != null) {
                _height.value = data.first
                _weight.value = data.second
            }
        }
    }

    /**
     * Reset save status
     */
    fun resetSaveStatus() {
        _saveStatus.value = HealthSaveStatus.IDLE
    }
}
