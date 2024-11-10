package com.example.android_kotlin_project.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapter.ItemAdapterNewsSport
import com.example.android_kotlin_project.models.SportNews

class SportActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewSport)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = listOf(
            SportNews(1, "title 1", "hello this is a subtitle", "n1"),
            SportNews(2, "title 2", "hello this is a subtitle", "n1"),
            SportNews(3, "title 3", "hello this is a subtitle", "n1"),
            SportNews(4, "title 4", "hello this is a subtitle", "n1")
            )
        val adapter = ItemAdapterNewsSport(items)
        recyclerView.adapter = adapter
    }
}
