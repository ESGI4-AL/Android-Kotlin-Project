package com.example.android_kotlin_project.repositories

import android.util.Log
import com.example.android_kotlin_project.models.Dao.RecipeDao
import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Entities.Recipe
import com.example.android_kotlin_project.models.Entities.RecipeList
import com.example.android_kotlin_project.retrofit.RetrofitInstance
import com.example.android_kotlin_project.utils.toEntity


class RecipeRepository(private val recipeDao: RecipeDao) {

    // Fetch all saved recipes as RecipeList
    suspend fun getSavedRecipes(): RecipeList {
        val recipeEntities = recipeDao.getAllRecipes()
        return RecipeList(recipeEntities) // Wrap in RecipeList
    }

    // Save multiple recipes from a RecipeList
    suspend fun saveRecipes(recipeList: RecipeList) {
        recipeDao.insertRecipes(recipeList.recipeEntities) // Insert recipes
    }

    // Save a single recipe
    suspend fun saveRecipe(recipeDto: RecipeDto) {
        val recipeEntity = recipeDto.toEntity(
        )
        recipeDao.insertRecipes(listOf(recipeEntity)) // Convert and insert as a list
    }

    // Clear all recipes
    suspend fun clearAllRecipes() {
        recipeDao.deleteAllRecipes()
    }

    suspend fun fetchRandomRecipe(): Recipe? {
        return try {
            Log.d("API_DEBUG", "Fetching random recipe...")

            val response = RetrofitInstance.api.getRandomRecipe().execute()

            if (response.isSuccessful && response.body() != null) {
                val recipeDto = response.body()!!.recipes.firstOrNull()
                Log.d("API_DEBUG", "API Response: ${response.body()}") // Log full response

                recipeDto?.toEntity() // Convert DTO to Entity
            } else {
                Log.e("API_ERROR", "Request failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Network request failed: ${e.message}")
            null
        }
    }


}
