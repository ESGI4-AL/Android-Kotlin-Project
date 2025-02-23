package com.example.android_kotlin_project.utils

object TextFormatter {
    fun formatInstructions(instructions: String): String {
        return instructions
            .replace("<ol>", "")
            .replace("</ol>", "")
            .replace("<li>", "\n\n• ")
            .replace("</li>", "")
            .replace("<p>", "")
            .replace("</p>", "")
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\n\\s*\n\\s*\n"), "\n\n")
            .trim()}
}