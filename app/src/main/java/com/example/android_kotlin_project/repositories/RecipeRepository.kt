package com.example.android_kotlin_project.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android_kotlin_project.models.Dao.RecipeDao
import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Entities.Recipe
import com.example.android_kotlin_project.models.Entities.RecipeList
import com.example.android_kotlin_project.retrofit.RetrofitInstance
import com.example.android_kotlin_project.utils.toEntity
import com.example.android_kotlin_project.utils.toEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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


    private val _randomRecipe = MutableLiveData<Recipe?>()
    val randomRecipe: LiveData<Recipe?> get() = _randomRecipe

    suspend fun fetchRandomRecipe(): Recipe? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getRandomRecipe().execute()
                if (response.isSuccessful && response.body() != null) {
                    val recipeDtos = response.body()!!.recipes
                    if (!recipeDtos.isNullOrEmpty()) {
                        return@withContext recipeDtos.toEntityList().first()
                    } else {
                        Log.e("API_ERROR", "No recipes found")
                        return@withContext null
                    }
                } else {
                    Log.e("API_ERROR", "Response unsuccessful: ${response.message()}")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "API call failed: ${e.message}")
                return@withContext null
            }
        }
    }
    fun getCurrentRandomRecipe(): Recipe? {
        return _randomRecipe.value
    }
}
