package com.example.android_kotlin_project.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android_kotlin_project.models.Dao.RecipeDao
import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Dto.RecipeListDto
import com.example.android_kotlin_project.models.Entities.Recipe
import com.example.android_kotlin_project.models.Entities.RecipeList
import com.example.android_kotlin_project.retrofit.RetrofitInstance
import com.example.android_kotlin_project.utils.toEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    private val _randomRecipe = MutableLiveData<Recipe>()
    val randomRecipe: LiveData<Recipe> get() = _randomRecipe

    fun fetchRandomRecipe() {
        RetrofitInstance.api.getRandomRecipe().enqueue(object : Callback<RecipeListDto> {
            override fun onResponse(call: Call<RecipeListDto>, response: Response<RecipeListDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val recipeDto = response.body()!!.recipes.firstOrNull()
                    _randomRecipe.value = recipeDto?.toEntity()
                } else {
                    Log.e("API_ERROR", "Response failed: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<RecipeListDto>, t: Throwable) {
                Log.e("API_ERROR", "Network request failed: ${t.message}")
            }
        })
    }

}
