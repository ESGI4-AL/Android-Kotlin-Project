package com.example.android_kotlin_project.ui.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Entities.Recipe
import com.example.android_kotlin_project.models.Entities.RecipeList
import com.example.android_kotlin_project.repositories.RecipeRepository
import com.example.android_kotlin_project.utils.RecipeUiState
import com.example.android_kotlin_project.utils.TextFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _randomRecipesLiveData = MutableLiveData<List<Recipe>?>()
    private val _savedRecipes = MutableStateFlow<RecipeList>(RecipeList(emptyList()))
    private val _randomRecipeLiveData = MutableLiveData<Recipe?>() // Private MutableLiveData
    val randomRecipeLiveData: MutableLiveData<Recipe?> get() = _randomRecipeLiveData // Public LiveData
    val error = MutableLiveData<String>()

    private val _recipeUiState = MutableLiveData<RecipeUiState>()
    val recipeUiState: LiveData<RecipeUiState> = _recipeUiState

    init {

        getPopularRecipes()
    }
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

    fun observeRandomRecipeLiveData(): MutableLiveData<Recipe?> {
        return randomRecipeLiveData
    }

    private val _recipeByIdLiveData = MutableLiveData<Recipe>()
    private val _popularRecipes = MutableLiveData<List<Recipe>>()
    val popularRecipes: MutableLiveData<List<Recipe>> = _popularRecipes

    @SuppressLint("NullSafeMutableLiveData")
    fun fetchRecipeById(recipeId: Int) {
        viewModelScope.launch {
            try {
                val recipe = withContext(Dispatchers.IO) {
                    repository.getRecipeById(recipeId)
                }

                _recipeByIdLiveData.postValue(recipe!!)
                _recipeUiState.postValue(
                    if (recipe != null) {
                        RecipeUiState(
                            title = recipe.title,
                            image = recipe.image,
                            readyInMinutes = recipe.readyInMinutes,
                            healthScore = recipe.healthScore,
                            summary=TextFormatter.formatSummary(recipe.summary),
                            instructions = TextFormatter.formatInstructions(
                                recipe.instructions ?: "No instructions found"
                            )
                        )
                    } else {
                        RecipeUiState(error = "Recipe not found")
                    }
                )
            } catch (e: Exception) {
                _recipeByIdLiveData.postValue(null)
                _recipeUiState.postValue(
                    RecipeUiState(error = e.message ?: "Unknown error occurred")
                )

            }
        }
    }

    fun getPopularRecipes() {
        viewModelScope.launch {
            try {
                val recipes = repository.fetchRandomRecipes()
                _popularRecipes.value = recipes!!
                Log.d("ViewModel", "Popular recipes fetched: ${recipes.size}")

            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error fetching popular recipes", e)
                _popularRecipes.value= emptyList()
            }
        }
    }

    private val _recipesByCategoryLiveData = MutableLiveData<List<Recipe>>()
    val recipesByCategoryLiveData: LiveData<List<Recipe>> get() = _recipesByCategoryLiveData

    fun loadRecipesByCategory(tags: String) {
        repository.fetchRandomRecipesByCategory(tags).observeForever { recipeList ->
            _recipesByCategoryLiveData.value = recipeList
        }
    }

}


