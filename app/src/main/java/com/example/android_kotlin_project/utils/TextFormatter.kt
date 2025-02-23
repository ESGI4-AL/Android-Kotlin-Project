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
    fun formatSummary(summary: String?): String {
        if (summary.isNullOrEmpty()) return "No summary available"

        return summary
            .replace("<b>", "")  // Remove bold tags
            .replace("</b>", "")
            // Remove HTML links
            .replace(Regex("<a href=.*?</a>"), "")
            .trim()
    }
}