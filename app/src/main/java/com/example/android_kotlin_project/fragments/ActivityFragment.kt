package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapters.NewsAdapter
import com.example.android_kotlin_project.data.NewsRepository
import android.content.Intent
import com.example.android_kotlin_project.activities.YogaActivity

class ActivityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yogaCard = view.findViewById<LinearLayout>(R.id.yogaCard)
        yogaCard.setOnClickListener {
            val intent = Intent(requireContext(), YogaActivity::class.java)
            startActivity(intent)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        NewsRepository.fetchNews { newsList ->
            if (newsList.isEmpty()) {
                Toast.makeText(requireContext(), "Aucune actualité disponible", Toast.LENGTH_SHORT).show()
            } else {
                recyclerView.adapter = NewsAdapter(requireContext(), newsList)
            }
        }
    }
}
