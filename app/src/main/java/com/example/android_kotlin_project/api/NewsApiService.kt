package com.example.android_kotlin_project.api

import com.example.android_kotlin_project.models.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    fun getNews(
        @Query("q") query: String = "fitness OR yoga OR sport",
        @Query("language") language: String = "fr",
        @Query("apiKey") apiKey: String = "6ed0727d4bc0465e8a3dab25253f3865"
    ): Call<NewsResponse>
}