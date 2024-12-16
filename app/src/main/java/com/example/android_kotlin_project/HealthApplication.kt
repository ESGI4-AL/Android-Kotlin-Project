package com.example.android_kotlin_project

import android.app.Application
import android.content.Context
import com.example.android_kotlin_project.utils.LocaleHelper
import java.util.Locale

class HealthApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(base, LocaleHelper.getLanguage(base)))
    }

    override fun onCreate() {
        super.onCreate()

        val locale = Locale("en")
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}