package com.example.android_kotlin_project.models.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android_kotlin_project.models.Entities.Recipe

@Dao
interface RecipeDao {

    // Fetch all saved recipes
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>

    // Insert multiple recipes as a list
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    // Clear all recipes from the database
    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()
}
