package com.example.android_kotlin_project.utils

import com.example.android_kotlin_project.models.Dto.RecipeDto
import com.example.android_kotlin_project.models.Entities.Recipe

fun RecipeDto.toEntity(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title!!,
        image = this.image!!,
        readyInMinutes = this.readyInMinutes,
        servings = this.servings!!,
        instructions = this.instructions ?: "",
        healthScore = this.healthScore!!,
        vegetarian = this.vegetarian,
        vegan = this.vegan,
        extendedIngredients = this.extendedIngredients,
        summary= this.summary
    )
}

fun Recipe.toDto(): RecipeDto {
    return RecipeDto(
        aggregateLikes = 0, // Defaults for missing fields
        analyzedInstructions = emptyList(),
        cheap = false,
        cookingMinutes = null,
        creditsText = "",
        cuisines = emptyList(),
        dairyFree = false,
        diets = emptyList(),
        dishTypes = emptyList(),
        extendedIngredients = emptyList(),
        gaps = "",
        glutenFree = false,
        healthScore = this.healthScore,
        id = this.id,
        image = this.image,
        imageType = "",
        instructions = this.instructions,
        license = "",
        lowFodmap = false,
        occasions = emptyList(),
        originalId = null,
        preparationMinutes = null,
        pricePerServing = 0.0,
        readyInMinutes = this.readyInMinutes,
        servings = this.servings,
        sourceName = "",
        sourceUrl = "",
        spoonacularScore = 0.0,
        spoonacularSourceUrl = "",
        summary = this.summary,
        sustainable = false,
        title = this.title,
        vegan = this.vegan,
        vegetarian = this.vegetarian,
        veryHealthy = false,
        veryPopular = false,
        weightWatcherSmartPoints = 0
    )
}

fun List<RecipeDto>.toEntityList(): List<Recipe> {
    return this.map { it.toEntity() }
}
