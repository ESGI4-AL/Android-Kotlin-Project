package com.example.android_kotlin_project.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.activities.PermissionsRationaleActivity
import com.example.android_kotlin_project.adapters.FlagSpinnerAdapter
import com.example.android_kotlin_project.databinding.FragmentSettingsBinding
import com.example.android_kotlin_project.utils.HealthConnectAvailability
import com.example.android_kotlin_project.viewmodels.HealthViewModel
import java.util.Locale

class SettingsFragment : Fragment() {

    private var isInitialSelection = true
    private lateinit var healthViewModel: HealthViewModel
    private lateinit var settingsBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return settingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageSpinner(view)

        settingsBinding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        settingsBinding.darkModeSwitch.isChecked = isDarkMode

        settingsBinding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("dark_mode", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("dark_mode", false)
            }
            editor.apply()
        }

        healthViewModel = ViewModelProvider(requireActivity())[HealthViewModel::class.java]

        healthViewModel.permissionStatus.observe(viewLifecycleOwner) { status ->
            settingsBinding.permissionStatus.text = status
        }


        healthViewModel.availability.observe(viewLifecycleOwner) { availabilityStatus ->
            when (availabilityStatus) {
                HealthConnectAvailability.INSTALLED -> {}
                HealthConnectAvailability.NOT_SUPPORTED -> {
                    settingsBinding.permissionStatus.text =
                        getString(R.string.health_connect_not_supported)
                }
                HealthConnectAvailability.NOT_INSTALLED -> {
                    settingsBinding.permissionStatus.text =
                        getString(R.string.health_connect_not_installed)
                }
            }
        }

        settingsBinding.dataPolicyLink.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                startActivity(Intent(requireContext(), PermissionsRationaleActivity::class.java))
            }
        }
    }

    private fun setupLanguageSpinner(view: View) {
        val languageSpinner = view.findViewById<Spinner>(R.id.language)
        val languages = resources.getStringArray(R.array.languages).toList()
        val flags = listOf(
            R.drawable.flag_uk,
            R.drawable.flag_frensh,
            R.drawable.flag_spanish,
            R.drawable.flag_chinese
        )

        val adapter = FlagSpinnerAdapter(requireContext(), languages, flags)
        languageSpinner.adapter = adapter

        val currentLocale = requireContext().resources.configuration.locales[0].language
        val position = when (currentLocale) {
            "en" -> 0
            "fr" -> 1
            "es" -> 2
            "zh" -> 3
            else -> 0
        }
        languageSpinner.setSelection(position)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isInitialSelection) {
                    isInitialSelection = false
                    return
                }

                val selectedLanguage = when (position) {
                    0 -> "en"
                    1 -> "fr"
                    2 -> "es"
                    3 -> "zh"
                    else -> "en"
                }
                if (currentLocale != selectedLanguage) {
                    updateLocale(selectedLanguage)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = requireContext().resources.configuration
        config.setLocale(locale)

        requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putString("language", languageCode)
            .apply()

        activity?.let { activity ->
            activity.createConfigurationContext(config)
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
            activity.recreate()
        }
    }

}