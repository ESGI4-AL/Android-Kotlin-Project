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
import com.example.android_kotlin_project.utils.toEntityList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {





    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    // MutableStateFlow for the list of recipes
    private val _savedRecipes = MutableStateFlow<RecipeList>(RecipeList(emptyList()))
    val savedRecipes: StateFlow<RecipeList> get() = _savedRecipes

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
    var randomRecipeLiveData = MutableLiveData<Recipe>()

    fun getRandomRecipe() {
        RetrofitInstance.api.getRandomRecipe().enqueue(object : Callback<RecipeListDto> {
            override fun onResponse(call: Call<RecipeListDto>, response: Response<RecipeListDto>) {
                if (response.isSuccessful && response.body() != null) {
                    val recipeDtos = response.body()!!.recipes
                    if (!recipeDtos.isNullOrEmpty()) {
                        val recipeEntities = recipeDtos.toEntityList()
                        if (recipeEntities.isNotEmpty()) {
                            randomRecipeLiveData.postValue(recipeEntities[0]) // Use postValue for background thread safety
                        } else {
                            Log.e("DEBUG", "Converted recipe list is empty")
                        }
                    } else {
                        Log.e("DEBUG", "Recipe list is empty")
                    }
                } else {
                    Log.e("DEBUG", "API Error: ${response.code()} - ${response.message()}")
                    _error.postValue("API Error: ${response.code()} - ${response.message()}") // Notify UI
                }
            }

            override fun onFailure(call: Call<RecipeListDto>, t: Throwable) {
                Log.e("DEBUG", "API call failed: ${t.message}")
                _error.postValue("Network Error: ${t.message}") // Notify UI
            }
        })
    }


    fun observeRandomRecipeLiveData(): LiveData<Recipe> {
        return randomRecipeLiveData
    }




}

