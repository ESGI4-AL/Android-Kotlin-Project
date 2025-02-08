package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R
import com.google.android.material.appbar.CollapsingToolbarLayout

class RecipeDetailsFragment : Fragment() {
    private var recipeId: Int? = null
    private var recipeTitle: String? =null
    private var recipeImage: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Debug
        Log.d("RecipeDetailsFragment", "onCreate called")

        arguments?.let {
            recipeId = it.getInt("RECIPE_ID")
            recipeTitle = it.getString("RECIPE_TITLE")
            recipeImage = it.getString("RECIPE_IMAGE")
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_details, container, false)
        val collapsingToolbar: CollapsingToolbarLayout = view.findViewById(R.id.collapsing_TB)
        collapsingToolbar.title = recipeTitle

        val recipeImageView: ImageView = view.findViewById(R.id.img_recipe_detail)
        if(!recipeImage.isNullOrEmpty()){
            Glide.with(this)
                .load(recipeImage)
                .placeholder(R.drawable.placeholder_img)
                .into(recipeImageView)

            val recipeImageView: ImageView = view.findViewById(R.id.img_recipe_detail)
            Log.d("RecipeDetailsFragment", "ImageView found: $recipeImageView")

        }



        return view
    }


}

