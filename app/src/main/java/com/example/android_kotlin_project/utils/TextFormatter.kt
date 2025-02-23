package com.example.android_kotlin_project.utils

object TextFormatter {
    fun formatInstructions(instructions: String): String {
        return instructions
            .replace("<ol>", "")
            .replace("</ol>", "")
            .replace("<li>", "\n\n• ")
            .replace("</li>", "")
            .trim()}
}