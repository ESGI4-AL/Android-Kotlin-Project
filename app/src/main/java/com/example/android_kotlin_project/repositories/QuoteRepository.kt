package com.example.android_kotlin_project.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuoteRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val quotesCollection = firestore.collection("quotes")

    suspend fun getRandomQuote(): Map<String, String>? {
        val snapshot = quotesCollection.get().await()
        val quotes = snapshot.documents.mapNotNull { document ->
            document.data?.filterValues { it is String }?.mapValues { it.value as String }
        }
        return quotes.randomOrNull()
    }
}
