package com.example.android_kotlin_project.views

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.android_kotlin_project.R

class BmiView(private val view: View) {
    private val bmiValueText: TextView = view.findViewById(R.id.bmi_value)
    private val bmiCategoryText: TextView = view.findViewById(R.id.bmi_category)
    private val bmiIndicator: ImageView = view.findViewById(R.id.bmi_indicator)
    private val bmiScaleContainer: LinearLayout = view.findViewById(R.id.bmi_scale_container)

    fun updateBmiDisplay(bmi: Float) {
        bmiValueText.text = String.format("%.1f", bmi)

        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal weight"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
        bmiCategoryText.text = category

        val totalWidth = bmiScaleContainer.width.toFloat()
        val position = when {
            bmi <= 40 -> (bmi / 40f) * totalWidth
            else -> totalWidth
        }

        // Animate the indicator to its position based on the bmi value
        bmiIndicator.animate()
            .translationX(position - (bmiIndicator.width / 2))
            .setDuration(300)
            .start()
    }
}
