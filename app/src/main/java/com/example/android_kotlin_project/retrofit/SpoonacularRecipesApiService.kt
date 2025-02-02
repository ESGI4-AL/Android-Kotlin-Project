package com.example.android_kotlin_project.retrofit

import com.example.android_kotlin_project.models.Dto.RecipeListDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularRecipesApiService {
    @GET("recipes/random")
     fun getRandomRecipe (
        //hardcoded but needs to be a secured var env
        @Query("apiKey") apiKey: String="0b6f5ac409fd45f9a84267d65de75be9",
        @Query("number") number: Int=1
    ): Call<RecipeListDto>

}