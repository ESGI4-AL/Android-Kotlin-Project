package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.dataBase.MyDatabase
import com.example.android_kotlin_project.repositories.RecipeRepository
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModel
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModelFactory

class RecipesFragment : Fragment() {

    private lateinit var recipeImageView: ImageView
    private lateinit var recipeMvvm: RecipeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeImageView = view.findViewById(R.id.RandomRecipeIV)

        val recipeDao = MyDatabase.getInstance(requireContext()).recipeDao()
        val repository = RecipeRepository(recipeDao)
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeMvvm = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]

        observerRandomRecipe()

        recipeMvvm.getRandomRecipe()

        onRandomRecipeClick(view)
    }

    private fun observerRandomRecipe() {
        Log.d("DEBUG_CHECK", "Observer started!")

        recipeMvvm.observeRandomRecipeLiveData().observe(viewLifecycleOwner) { recipe ->
            Log.d("DEBUG_CHECK", "Observer triggered with recipe: $recipe")

            val imageUrl = recipe?.image
            Log.d("GLIDE_DEBUG", "Loading Image URL: $imageUrl")

            Glide.with(this)
                .load(if (imageUrl.isNullOrEmpty()) R.drawable.placeholder_img else imageUrl)
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(recipeImageView)

            Log.d("GLIDE_DEBUG", "Glide should be loading now.")
        }
    }


    private fun onRandomRecipeClick(view: View) {
        val recipeCard = view.findViewById<View>(R.id.RandomRecipeIV)

        recipeCard.setOnClickListener {
            val randomRecipe = recipeMvvm.randomRecipeLiveData.value
            if (randomRecipe != null) {
                val bundle = Bundle().apply {
                    putInt("RECIPE_ID", randomRecipe.id)
                    putString("RECIPE_TITLE", randomRecipe.title)
                    putString("RECIPE_IMAGE", randomRecipe.image)
                }

                findNavController().navigate(R.id.recipeDetailsFragment, bundle)
            } else {
                Log.e("NAVIGATION_ERROR", "Recipe is null, cannot navigate.")
                Toast.makeText(requireContext(), "Recipe not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
