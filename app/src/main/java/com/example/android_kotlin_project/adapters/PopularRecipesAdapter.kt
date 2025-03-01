package com.example.android_kotlin_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.Entities.Recipe

class PopularRecipesAdapter(
    private var recipes: List<Recipe> = emptyList(),
    private val onRecipeClickListener: (Recipe) -> Unit
) : RecyclerView.Adapter<PopularRecipesAdapter.RecipeViewHolder>(){

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImageView: ImageView = itemView.findViewById(R.id.img_popular_recipe_item)
    }

    fun updateRecipes(newRecipes: List<Recipe>) {
        this.recipes = newRecipes
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.popular_items, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .placeholder(R.drawable.placeholder_img)
            .error(R.drawable.placeholder_img)
            .centerCrop()
            .into(holder.recipeImageView)

        // Set click listener
        holder.itemView.setOnClickListener {
            onRecipeClickListener(recipe)
        }
    }

    override fun getItemCount(): Int = recipes.size

}