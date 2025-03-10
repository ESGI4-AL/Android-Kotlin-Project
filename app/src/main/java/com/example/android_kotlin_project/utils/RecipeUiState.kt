package com.example.android_kotlin_project.utils

data class RecipeUiState( val title: String = "",
                          val image: String = "",
                          val instructions: String = "",
                          val error: String? = null,
                          val readyInMinutes: Int =0 ,
                          val healthScore: Int =0,
                          val summary : String=""
)
