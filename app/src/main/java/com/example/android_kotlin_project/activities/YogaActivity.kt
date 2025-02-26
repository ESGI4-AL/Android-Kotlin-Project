package com.example.android_kotlin_project.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapters.YogaAdapter
import com.example.android_kotlin_project.dialog.YogaTimerDialog
import com.example.android_kotlin_project.repositories.YogaRepository

class YogaActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var timerTextView: TextView
    private val yogaRepository = YogaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga)
        recyclerView = findViewById(R.id.recycler_exercises)
        timerTextView = findViewById(R.id.timer_text)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        loadExercises()
    }

    private fun loadExercises() {
        yogaRepository.fetchExercises { exercises ->
            if (exercises.isEmpty()) {
                Toast.makeText(this, "Aucun exercice disponible", Toast.LENGTH_SHORT).show()
            } else {
                recyclerView.adapter = YogaAdapter(exercises, supportFragmentManager)
                timerTextView.setOnClickListener {
                    val dialog = YogaTimerDialog(exercises)
                    dialog.show(supportFragmentManager, "YogaTimerDialog")
                }
            }
        }
    }
}
