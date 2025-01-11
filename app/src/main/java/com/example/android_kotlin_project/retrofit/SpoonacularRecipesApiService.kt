package com.example.android_kotlin_project.retrofit

import com.example.android_kotlin_project.models.RecipeList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularRecipesApiService {
    @GET("recipes/random")
    fun getRandomRecipe(
        //hardcoded but needs to be a secured var env
        @Query("apiKey") apiKey: String="8a31aa6ebbe44cd8a22fcb4f9571a286",
        @Query("number") number: Int=1
    ):Call<RecipeList>
}