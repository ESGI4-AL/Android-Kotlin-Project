package com.example.android_kotlin_project.models.Dto

import com.google.gson.annotations.SerializedName

data class RecipeListDto(@SerializedName("recipes") val recipes: List<RecipeDto>)


