package com.example.android_kotlin_project.repositories

import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Recipe
import com.example.android_kotlin_project.models.RecipeList
import com.example.android_kotlin_project.retrofit.SpoonacularRecipesApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeRepository(private val api: SpoonacularRecipesApiService) {
    fun getRandomRecipe(onSuccess: (RecipeDto) -> Unit, onError: (String) -> Unit) {
        api.getRandomRecipe().enqueue(object : Callback<RecipeList> {
            override fun onResponse(call: Call<RecipeList>, response: Response<RecipeList>) {
                if (response.isSuccessful && response.body() != null) {
                    val recipeDto = response.body()!!.recipes[0].toRecipeDto()
                    onSuccess(recipeDto)
                } else {
                    onError("Error fetching recipe")
                }
            }

            override fun onFailure(call: Call<RecipeList>, t: Throwable) {
                onError(t.message ?: "Network error")
            }
        })
    }

    private fun Recipe.toRecipeDto() = RecipeDto(
        id = id,
        title = title,
        image = image,
        readyInMinutes = readyInMinutes,
        servings = servings,
        instructions = instructions,
        extendedIngredients = extendedIngredients,
        healthScore = healthScore,
        vegetarian = vegetarian,
        vegan = vegan
    )
}



