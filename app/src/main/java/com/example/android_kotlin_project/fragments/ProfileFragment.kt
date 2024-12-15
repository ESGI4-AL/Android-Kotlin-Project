package com.example.android_kotlin_project.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.android_kotlin_project.R
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var isInitialSelection = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLanguageSpinner(view)
    }

    private fun setupLanguageSpinner(view: View) {
        val languageSpinner = view.findViewById<Spinner>(R.id.language)

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