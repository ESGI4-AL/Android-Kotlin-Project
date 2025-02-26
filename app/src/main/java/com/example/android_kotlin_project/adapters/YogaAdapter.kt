package com.example.android_kotlin_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.YogaExercise

class YogaAdapter(private val exercises: List<YogaExercise>) :
    RecyclerView.Adapter<YogaAdapter.YogaViewHolder>() {

    class YogaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseImage: ImageView = view.findViewById(R.id.exercise_image)
        val exerciseName: TextView = view.findViewById(R.id.exercise_name)
        val exerciseDescription: TextView = view.findViewById(R.id.exercise_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.yoga_item, parent, false)
        return YogaViewHolder(view)
    }

    override fun onBindViewHolder(holder: YogaViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        holder.exerciseDescription.text = exercise.description
        Glide.with(holder.itemView.context)
            .load(exercise.imageUrl)
            .into(holder.exerciseImage)
    }

    override fun getItemCount() = exercises.size
}
