package com.example.android_kotlin_project.retrofit

import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Dto.RecipeListDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularRecipesApiService {
    @GET("recipes/random")
     fun getRandomRecipe (
        //hardcoded but needs to be a secured var env
        @Query("apiKey") apiKey: String="7c1d68022f50446bb19f40f722671ca7",
        @Query("number") number: Int=1
    ): Call<RecipeListDto>


    @GET("recipes/{id}/information")
    fun getRecipeById(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String="7c1d68022f50446bb19f40f722671ca7",
        @Query("includeNutrition") includeNutrition: Boolean = false

    ): Call<RecipeDto>

    @GET("recipes/random")
    fun getRandomRecipes(
        @Query("apiKey") apiKey: String="7c1d68022f50446bb19f40f722671ca7",
        @Query("number") number: Int=10
    ): Call<RecipeListDto>

//    @GET("recipes/random")
//    suspend fun getRandomRecipes(
//        @Query("number") number: Int,
//        @Query("tags") tags: String? = null,
//        @Query("apiKey") apiKey: String = API_KEY
//    ): Response<RecipeResponse>
}