package com.example.android_kotlin_project.models.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android_kotlin_project.models.ExtendedIngredient
import com.google.gson.annotations.SerializedName

@Entity(tableName ="recipes")

data class Recipe(
    @PrimaryKey val id: Int,
    val title: String ="Unknown",
    val image: String ="",
    val readyInMinutes: Int,
    val servings: Int =0,
    @SerializedName("instructions")
    val instructions: String?,
    val extendedIngredients: List<ExtendedIngredient> = emptyList(),
    val healthScore: Int =0,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val summary: String
)