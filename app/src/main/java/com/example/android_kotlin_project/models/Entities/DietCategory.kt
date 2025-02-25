package com.example.android_kotlin_project.models.Entities

enum class DietCategory (val tag: String){
    VEGAN("vegan"),
    VEGETARIAN("vegetarian"),
    GLUTEN_FREE("gluten-free"),
    DAIRY_FREE("dairy-free"),
    PESCETARIAN("pescetarian"),
    PALEO("paleo"),
    KETO("ketogenic"),

    // Meal types/courses
    MAIN_COURSE("main course"),
    SIDE_DISH("side dish"),
    DESSERT("dessert"),
    APPETIZER("appetizer"),
    SALAD("salad"),
    BREAKFAST("breakfast"),
    SOUP("soup"),

    // Cuisines
    ITALIAN("italian"),
    MEXICAN("mexican"),
    ASIAN("asian"),
    MEDITERRANEAN("mediterranean"),
    INDIAN("indian");

    companion object {
        fun fromTag(tag: String): DietCategory? {
            return values().find { it.tag == tag }
        }
    }
}