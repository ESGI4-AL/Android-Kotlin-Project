package com.example.android_kotlin_project.models.Dto

import com.example.android_kotlin_project.models.ExtendedIngredient

data class RecipeDto(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val instructions: String,
    val extendedIngredients: List<ExtendedIngredient>,
    val healthScore: Int,
    val vegetarian: Boolean,
    val vegan: Boolean
)