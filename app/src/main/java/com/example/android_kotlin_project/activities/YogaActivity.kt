package com.example.android_kotlin_project.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapters.YogaAdapter

class YogaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_exercises)
        recyclerView.layoutManager = GridLayoutManager(this, 2)


        val yogaData = com.example.android_kotlin_project.data.YogaData
        val exercises = yogaData.getExercises()
        recyclerView.adapter = YogaAdapter(exercises)
    }
}
