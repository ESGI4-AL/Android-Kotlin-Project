package com.example.android_kotlin_project.dataBase

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.android_kotlin_project.models.ExtendedIngredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun fromString(value: String): List<ExtendedIngredient> {
        val listType = object : TypeToken<List<ExtendedIngredient>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<ExtendedIngredient>): String {
        return Gson().toJson(list)
    }
}