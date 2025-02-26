package com.example.android_kotlin_project.data

import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.YogaExercise

object YogaData {
    fun getExercises(): List<YogaExercise> {
        return listOf(
            YogaExercise("Posture de l'arbre", "Améliore l'équilibre et la concentration.", R.drawable.tree_post),
            YogaExercise("Posture du guerrier",  "Renforce les jambes et la stabilité.", R.drawable.warrior_post),
            YogaExercise("Posture du cobra",  "Étirer la colonne vertébrale et ouvrir la poitrine.", R.drawable.cobra_post),
            YogaExercise("Posture du chien tête en bas",  "Améliore la souplesse des jambes et du dos.", R.drawable.dog_post),
            YogaExercise("Posture de l'arbre", "Améliore l'équilibre et la concentration.", R.drawable.tree_post),
            YogaExercise("Posture du guerrier",  "Renforce les jambes et la stabilité.", R.drawable.warrior_post),
            YogaExercise("Posture du cobra",  "Étirer la colonne vertébrale et ouvrir la poitrine.", R.drawable.cobra_post),
            YogaExercise("Posture du chien tête en bas",  "Améliore la souplesse des jambes et du dos.", R.drawable.dog_post)


        )
    }
}