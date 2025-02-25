package com.example.android_kotlin_project.api

import com.example.android_kotlin_project.models.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    fun getNews(
        @Query("category") category: String = "health",
        @Query("apiKey") apiKey: String = "6ed0727d4bc0465e8a3dab25253f3865"
    ): Call<NewsResponse>
}