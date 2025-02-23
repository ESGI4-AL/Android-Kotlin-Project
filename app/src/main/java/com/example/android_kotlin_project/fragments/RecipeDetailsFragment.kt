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

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeInstructionsTextView: TextView
    private lateinit var collapsingToolbar: CollapsingToolbarLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        //inflate the layout
        val view = inflater.inflate(R.layout.fragment_recipe_details, container, false)

        initializeViews(view)
        setupInitialState()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeRecipeDetails()
        fetchRecipeDetails()


    }
    private fun initializeViews(view: View) {
        recipeImageView = view.findViewById(R.id.img_recipe_detail)
        recipeInstructionsTextView = view.findViewById(R.id.instructionsContentTV)
        collapsingToolbar = view.findViewById(R.id.collapsing_TB)
    }
    private fun setupInitialState() {
        collapsingToolbar.title = recipeTitle
        if (!recipeImage.isNullOrEmpty()) {
            loadImage(recipeImage!!)
        }
    }
    private fun setupViewModel() {
        val recipeDao = MyDatabase.getInstance(requireContext()).recipeDao()
        val repository = RecipeRepository(recipeDao)
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]
    }
    private fun fetchRecipeDetails() {
        recipeId?.let { id ->
            recipeViewModel.fetchRecipeById(id)
        } ?: run {
            Log.e("RecipeDetailsFragment", "No recipe ID passed!")
        }
    }
    private fun observeRecipeDetails() {
    recipeViewModel.recipeByIdLiveData.observe(viewLifecycleOwner) { recipe ->
        if (recipe != null) {
            Log.d("DEBUG_CHECK", "Recipe retrieved by ID: $recipe")

            collapsingToolbar.title = recipe.title
            recipeInstructionsTextView.text = recipe.instructions ?: "No instructions found"
            loadImage(recipe.image)
        } else {
            Log.e("DETAILS_ERROR", "Failed to load recipe details")
        }
    }
}

    private fun loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_img)
            .error(R.drawable.placeholder_img)
            .into(recipeImageView)
    }


}

