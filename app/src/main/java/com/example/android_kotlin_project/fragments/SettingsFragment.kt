package com.example.android_kotlin_project.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapters.FlagSpinnerAdapter
import java.util.Locale

class SettingsFragment : Fragment() {

    private var isInitialSelection = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageSpinner(view)

        val themeSwitch = view.findViewById<Switch>(R.id.theme_switch)

        val backButton: ImageButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        themeSwitch.isChecked = isDarkMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
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
                    else -> "fr"
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