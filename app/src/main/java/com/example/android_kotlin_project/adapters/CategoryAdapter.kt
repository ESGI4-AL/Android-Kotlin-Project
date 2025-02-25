package com.example.android_kotlin_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.Entities.CategoryItem
import com.example.android_kotlin_project.models.Entities.DietCategory

class CategoryAdapter (private val categories: List<CategoryItem>,
                       private val onCategoryClick: (DietCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImageView: ImageView = itemView.findViewById(R.id.img_catgoryIV)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.category_nameTV)
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem = categories[position]

        // Load the image using Glide and apply a circle crop transformation
        Glide.with(holder.itemView.context)
            .load(categoryItem.imageId) // This can be a drawable resource or a URL
            .circleCrop()
            .into(holder.categoryImageView)

        // Set the category name
        holder.categoryNameTextView.text = categoryItem.name

        // Handle click events
        holder.itemView.setOnClickListener {
            onCategoryClick(categoryItem.dietCategory)
        }
    }}




