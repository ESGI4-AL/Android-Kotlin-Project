package com.example.android_kotlin_project.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.android_kotlin_project.R

class PrivacyPolicyFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Privacy Policy")
            .setMessage(getString(R.string.privacy_policy_text))
            .setPositiveButton("OK") { _, _ ->
                // Dismiss the dialog and close the activity
                dismiss()
                activity?.finish()
            }
            .create()
    }
}
