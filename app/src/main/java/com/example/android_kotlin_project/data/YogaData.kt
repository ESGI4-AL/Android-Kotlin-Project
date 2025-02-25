package com.example.android_kotlin_project.data

import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.YogaExercise

object YogaData {
    fun getExercises(): List<YogaExercise> {
        return listOf(
            YogaExercise("Posture de l'arbre", "3 min", R.drawable.yoga_icon, "Améliore l'équilibre et la concentration."),
            YogaExercise("Posture du guerrier", "5 min", R.drawable.yoga_icon, "Renforce les jambes et la stabilité."),
            YogaExercise("Posture du cobra", "4 min", R.drawable.yoga_icon, "Étirer la colonne vertébrale et ouvrir la poitrine."),
            YogaExercise("Posture du chien tête en bas", "6 min", R.drawable.yoga_icon, "Améliore la souplesse des jambes et du dos.")
        )
    }
}