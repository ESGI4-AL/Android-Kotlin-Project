package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.dataBase.MyDatabase
import com.example.android_kotlin_project.repositories.RecipeRepository
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModel
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModelFactory
import com.google.android.material.appbar.CollapsingToolbarLayout

class RecipeDetailsFragment : Fragment() {
    private var recipeId: Int? = null
    private var recipeTitle: String? =null
    private var recipeImage: String? =null
    private lateinit var recipeMvvm: RecipeViewModel
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeTitleTextView: TextView
    private lateinit var recipeInstructionsTextView: TextView

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeImageView = view.findViewById(R.id.img_recipe_detail)
        recipeInstructionsTextView = view.findViewById(R.id.instructionsContentTV)

        val repository = RecipeRepository(MyDatabase.getInstance(requireContext()).recipeDao())
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeMvvm = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]

        val recipeId = arguments?.getInt("RECIPE_ID") ?: return
        Log.d("RecipeDetailsFragment", "Fetching recipe with ID: $recipeId")

        recipeMvvm.fetchRecipeById(recipeId)

        observerRecipeDetails()
    }

    private fun observerRecipeDetails() {
        recipeMvvm.recipeByIdLiveData.observe(viewLifecycleOwner) { recipe ->
            if (recipe != null) {
                recipeTitleTextView.text = recipe.title
                recipeInstructionsTextView.text = recipe.instructions ?: "No instructions available."

                // Update the image using Glide
                Glide.with(this)
                    .load(recipe.image)
                    .placeholder(R.drawable.placeholder_img)
                    .error(R.drawable.placeholder_img)
                    .into(recipeImageView)
            } else {
                Log.e("DETAILS_ERROR", "Failed to load recipe details")
            }
        }
    }



}

