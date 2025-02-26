package com.example.android_kotlin_project.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.android_kotlin_project.R

class YogaTimerDialog(private val exerciseName: String) : DialogFragment() {

    private lateinit var timerTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var stopButton: Button
    private var countDownTimer: CountDownTimer? = null
    private val totalTime = 30000L  // 30 secondes
    private val interval = 1000L    // 1 seconde

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_yoga_timer, container, false)

        timerTextView = view.findViewById(R.id.timerTextView)
        progressBar = view.findViewById(R.id.progressBar)
        stopButton = view.findViewById(R.id.stopButton)

        progressBar.max = (totalTime / interval).toInt()

        startTimer()

        stopButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                timerTextView.text = "$secondsRemaining s"
                progressBar.progress = secondsRemaining
            }

            override fun onFinish() {
                timerTextView.text = "Terminé !"
                dismiss()
            }
        }.start()
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        super.onDestroyView()
    }
}
