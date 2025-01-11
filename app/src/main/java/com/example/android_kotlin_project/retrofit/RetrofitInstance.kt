package com.example.android_kotlin_project.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.spoonacular.com/"

    val api: SpoonacularRecipesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON parsing with Gson
            .build()
            .create(SpoonacularRecipesApiService::class.java) // Create the API service
    }
}
