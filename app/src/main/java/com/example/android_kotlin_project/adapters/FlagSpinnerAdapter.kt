package com.example.android_kotlin_project.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.android_kotlin_project.R

class FlagSpinnerAdapter(
    context: Context,
    private val languages: List<String>,
    private val flags: List<Int>
) : ArrayAdapter<String>(context, R.layout.spinner_item, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val flag: ImageView = view.findViewById(R.id.flag)
        val languageName: TextView = view.findViewById(R.id.language_name)

        flag.setImageResource(flags[position])
        languageName.text = languages[position]

        return view
    }
}