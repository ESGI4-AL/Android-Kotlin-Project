package com.example.android_kotlin_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.YogaExercise

class YogaAdapter(private val exercises: List<YogaExercise>) :
    RecyclerView.Adapter<YogaAdapter.YogaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.yoga_item, parent, false)
        return YogaViewHolder(view)
    }

    class YogaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseImage: ImageView = view.findViewById(R.id.exercise_image)
        val exerciseName: TextView = view.findViewById(R.id.exercise_name)
        val exerciseDuration: TextView = view.findViewById(R.id.exercise_duration)
        val exerciseDescription: TextView = view.findViewById(R.id.exerciseDescription)
    }

    override fun onBindViewHolder(holder: YogaViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseImage.setImageResource(exercise.imageResId)
        holder.exerciseName.text = exercise.name
        holder.exerciseDuration.text = exercise.duration
        holder.exerciseDescription.text = exercise.description
    }

    override fun getItemCount(): Int = exercises.size
}
