package com.example.android_kotlin_project.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TipRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val tipsCollection = firestore.collection("tips")

    suspend fun getRandomQuote(): String? {
        val snapshot = tipsCollection.get().await()
        val randomTip = snapshot.documents.random()
        return randomTip.getString("tip")
    }
}
