package com.example.android_kotlin_project.repositories

import android.content.Context
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.NewsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object NewsRepository {
    fun loadNews(context: Context): List<NewsItem> {
        val inputStream = context.resources.openRawResource(R.raw.news)
        val reader = InputStreamReader(inputStream)
        val newsType = object : TypeToken<List<NewsItem>>() {}.type
        return Gson().fromJson(reader, newsType)
    }
}