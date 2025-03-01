package com.example.android_kotlin_project.retrofit

import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Dto.RecipeListDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularRecipesApiService {
    companion object {
        const val API_KEY = "1c4aa20d8f964caea36e1886cc9a380c"
    }
    @GET("recipes/random")
     fun getRandomRecipe (
        //hardcoded but needs to be a secured var env
        @Query("apiKey") apiKey: String= API_KEY,
        @Query("number") number: Int=1
    ): Call<RecipeListDto>


    @GET("recipes/{id}/information")
    fun getRecipeById(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String= API_KEY,
        @Query("includeNutrition") includeNutrition: Boolean = false

    ): Call<RecipeDto>

    @GET("recipes/random")
    fun getRandomRecipes(
        @Query("apiKey") apiKey: String= API_KEY,
        @Query("number") number: Int=10
    ): Call<RecipeListDto>


    @GET("recipes/random")
    fun getRecipesByCategory(
        @Query("apiKey") apiKey: String= API_KEY,
        @Query("number") number: Int=20,
        @Query("tags") tags: String? = null

    ): Call<RecipeListDto>

}