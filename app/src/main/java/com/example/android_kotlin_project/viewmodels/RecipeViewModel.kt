package com.example.android_kotlin_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Entities.Recipe
import com.example.android_kotlin_project.models.Entities.RecipeList
import com.example.android_kotlin_project.repositories.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _savedRecipes = MutableStateFlow<RecipeList>(RecipeList(emptyList()))
    private val _randomRecipeLiveData = MutableLiveData<Recipe?>() // Private MutableLiveData
    val randomRecipeLiveData: MutableLiveData<Recipe?> get() = _randomRecipeLiveData // Public LiveData
    val error = MutableLiveData<String>()

    fun getRandomRecipe() {
        viewModelScope.launch {
            val recipe = repository.fetchRandomRecipe()
            if (recipe != null) {
                _randomRecipeLiveData.postValue(recipe)
            } else {
                Log.e("API_ERROR", "Failed to fetch recipe")
            }
        }
    }


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
//fun getRandomRecipe() {
//    Log.d("API_DEBUG", "Fetching random recipe...")
//
//    RetrofitInstance.api.getRandomRecipe().enqueue(object : Callback<RecipeListDto> {
//        override fun onResponse(call: Call<RecipeListDto>, response: Response<RecipeListDto>) {
//            Log.d("API_DEBUG", "Response Code: ${response.code()}")
//            Log.d("API_DEBUG", "Response Body: ${response.body()}")
//
//            if (response.isSuccessful && response.body() != null) {
//                val recipeDtos = response.body()!!.recipes
//                if (!recipeDtos.isNullOrEmpty()) {
//                    val recipe = recipeDtos[0] // Get first recipe
//                    Log.d("API_SUCCESS", "Recipe Title: ${recipe.title}")
//                    Log.d("API_SUCCESS", "Recipe Image URL: ${recipe.image}")
//
//                    val recipeEntity = recipe.toEntity() // Convert DTO to Entity
//                    randomRecipeLiveData.postValue(recipeEntity)
//                } else {
//                    Log.e("API_ERROR", "Recipe list is empty")
//                }
//            } else {
//                Log.e("API_ERROR", "API Error: ${response.code()} - ${response.message()}")
//            }
//        }
//
//        override fun onFailure(call: Call<RecipeListDto>, t: Throwable) {
//            Log.e("API_ERROR", "Network request failed: ${t.message}")
//        }
//    })
//}


//    fun getRandomRecipe() {
//        viewModelScope.launch {
//            repository.fetchRandomRecipe()
//        }
//    }

//    fun getRandomRecipe() {
//        viewModelScope.launch {
//            try {
//                repository.fetchRandomRecipe()
//                _randomRecipeLiveData.postValue(repository.getCurrentRandomRecipe())
//            } catch (e: Exception) {
//                error.postValue("Failed to fetch recipe: ${e.message}")
//            }
//        }
//    }
    fun observeRandomRecipeLiveData(): MutableLiveData<Recipe?> {
        return randomRecipeLiveData
    }




}

