package com.example.android_kotlin_project.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R

class YogaDescriptionDialog(
    private val exerciseName: String,
    private val exerciseLongDescription: String,
    private val exerciseImageUrl: String
) : DialogFragment() {

    private lateinit var exerciseImage: ImageView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var closePopup: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_yoga_description, container, false)

        exerciseImage = view.findViewById(R.id.exercise_image)
        title = view.findViewById(R.id.exercise_name)
        description = view.findViewById(R.id.exercise_longDescription)
        closePopup = view.findViewById(R.id.stopButton)

        title.text = exerciseName
        description.text = exerciseLongDescription
        Glide.with(requireContext()).load(exerciseImageUrl).into(exerciseImage)

        closePopup.setOnClickListener {
            dismiss()
        }

        return view
    }
}
