package com.example.android_kotlin_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Dto.RecipeListDto
import com.example.android_kotlin_project.models.Entities.Recipe
import com.example.android_kotlin_project.models.Entities.RecipeList
import com.example.android_kotlin_project.repositories.RecipeRepository
import com.example.android_kotlin_project.retrofit.RetrofitInstance
import com.example.android_kotlin_project.utils.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _error = MutableLiveData<String>()
    // MutableStateFlow for the list of recipes
    private val _savedRecipes = MutableStateFlow<RecipeList>(RecipeList(emptyList()))
    var randomRecipeLiveData = MutableLiveData<Recipe>()
    private val _randomRecipeLiveData = MutableLiveData<Recipe?>()
    val error: LiveData<String> get() = _error

    // Load saved recipes from the repository
    fun loadSavedRecipes() {
        viewModelScope.launch {
            val recipes = repository.getSavedRecipes()
            _savedRecipes.value = recipes // Update the StateFlow
        }
    }

    // Save multiple recipes from a RecipeList
    fun saveRecipes(recipeList: RecipeList) {
        viewModelScope.launch {
            repository.saveRecipes(recipeList)
            loadSavedRecipes() // Reload after saving
        }
    }

    // Save a single recipe
    fun saveRecipe(recipeDto: RecipeDto) {
        viewModelScope.launch {
            repository.saveRecipe(recipeDto)
            loadSavedRecipes() // Reload after saving
        }
    }

    // Clear all recipes
    fun clearRecipes() {
        viewModelScope.launch {
            repository.clearAllRecipes()
            _savedRecipes.value = RecipeList(emptyList()) // Clear StateFlow
        }
    }


//    fun getRandomRecipe() {
//        viewModelScope.launch {
//            val recipe = repository.fetchRandomRecipe()
//            if (recipe != null) {
//                _randomRecipeLiveData.postValue(recipe)
//            } else {
//                _error.postValue("Failed to fetch recipe")
//            }
//        }
//    }
fun getRandomRecipe() {
    Log.d("API_DEBUG", "Fetching random recipe...")

    RetrofitInstance.api.getRandomRecipe().enqueue(object : Callback<RecipeListDto> {
        override fun onResponse(call: Call<RecipeListDto>, response: Response<RecipeListDto>) {
            Log.d("API_DEBUG", "Response Code: ${response.code()}")
            Log.d("API_DEBUG", "Response Body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                val recipeDtos = response.body()!!.recipes
                if (!recipeDtos.isNullOrEmpty()) {
                    val recipe = recipeDtos[0] // Get first recipe
                    Log.d("API_SUCCESS", "Recipe Title: ${recipe.title}")
                    Log.d("API_SUCCESS", "Recipe Image URL: ${recipe.image}")

                    val recipeEntity = recipe.toEntity() // Convert DTO to Entity
                    randomRecipeLiveData.postValue(recipeEntity)
                } else {
                    Log.e("API_ERROR", "Recipe list is empty")
                }
            } else {
                Log.e("API_ERROR", "API Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<RecipeListDto>, t: Throwable) {
            Log.e("API_ERROR", "Network request failed: ${t.message}")
        }
    })
}


    fun observeRandomRecipeLiveData(): LiveData<Recipe> {
        return randomRecipeLiveData
    }




}

