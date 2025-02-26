package com.example.android_kotlin_project.repositories

import com.example.android_kotlin_project.models.YogaExercise
import com.google.firebase.firestore.FirebaseFirestore

class YogaRepository {
    private val db = FirebaseFirestore.getInstance()

    fun fetchExercises(callback: (List<YogaExercise>) -> Unit) {
        db.collection("exercises")
            .get()
            .addOnSuccessListener { documents ->
                val exercises = documents.map { doc ->
                    YogaExercise(
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        longDescription = doc.getString("longDescription") ?: "",
                    )
                }
                callback(exercises)
            }
    }
}

