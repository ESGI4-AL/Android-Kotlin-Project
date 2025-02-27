package com.example.android_kotlin_project.dialog

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.YogaExercise

class YogaTimerDialog(private val exercises: List<YogaExercise>) : DialogFragment() {

    private lateinit var exerciseImage: ImageView
    private lateinit var exerciseName: TextView
    private lateinit var timerText: TextView
    private lateinit var closeButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_yoga_timer, container, false)

        exerciseImage = view.findViewById(R.id.exercise_image)
        exerciseName = view.findViewById(R.id.exercise_name)
        timerText = view.findViewById(R.id.timer_text)
        closeButton = view.findViewById(R.id.stopButton)

        closeButton.setOnClickListener {
            countDownTimer?.cancel()
            dismiss()
        }
        startExerciseDisplay()
        return view
    }

    private fun startExerciseDisplay() {
        if (exercises.isEmpty()) {
            dismiss()
            return
        }

        updateExercise()
        countDownTimer = object : CountDownTimer((exercises.size * 60000L), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = ((millisUntilFinished % 60000) / 1000).toInt()
                timerText.text = "$secondsRemaining s"

                if (secondsRemaining == 0) {
                    nextExercise()
                }
            }
            override fun onFinish() {
                dismiss()
            }
        }.start()
    }

    private fun nextExercise() {
        if (currentIndex < exercises.size - 1) {
            currentIndex++
            updateExercise()
        } else {
            dismiss()
        }
    }

    private fun updateExercise() {
        val exercise = exercises[currentIndex]
        exerciseName.text = exercise.name
        Glide.with(requireContext()).load(exercise.imageUrl).into(exerciseImage)
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        super.onDestroyView()
    }
}
