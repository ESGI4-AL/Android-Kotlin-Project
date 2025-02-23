package com.example.android_kotlin_project.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android_kotlin_project.fragments.PrivacyPolicyFragment

/**
 * Activity to show the privacy policy dialog
 */
class PermissionsRationaleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launch the PrivacyPolicyFragment
        if (savedInstanceState == null) {
            val fragment = PrivacyPolicyFragment()
            fragment.show(supportFragmentManager, "PrivacyPolicyFragment")
        }
    }

    /**
     * Close activity when back button is pressed
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
