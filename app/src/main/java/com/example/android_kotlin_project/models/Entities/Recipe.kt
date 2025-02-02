package com.example.android_kotlin_project.models.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android_kotlin_project.models.ExtendedIngredient

@Entity(tableName ="recipes")

data class Recipe(
    @PrimaryKey val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val instructions: String,
    val extendedIngredients: List<ExtendedIngredient>,
    val healthScore: Int,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val summary: String
)