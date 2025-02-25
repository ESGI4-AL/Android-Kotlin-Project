package com.example.android_kotlin_project.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapter.YogaAdapter
import com.example.android_kotlin_project.data.YogaData

class YogaActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewYoga)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = YogaAdapter(YogaData.getExercises())
    }
}