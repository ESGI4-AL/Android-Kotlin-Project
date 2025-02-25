package com.example.android_kotlin_project.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android_kotlin_project.models.NewsEntity

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Query("SELECT * FROM news_table")
    suspend fun getAllNews(): List<NewsEntity>

    @Query("DELETE FROM news_table")
    suspend fun clearNews()
}