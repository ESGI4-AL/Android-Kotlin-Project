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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapters.PopularRecipesAdapter
import com.example.android_kotlin_project.dataBase.MyDatabase
import com.example.android_kotlin_project.repositories.RecipeRepository
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModel
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModelFactory

class RecipesFragment : Fragment() {

    private lateinit var recipeImageView: ImageView
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var popularRecipesAdapter: PopularRecipesAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipeDao = MyDatabase.getInstance(requireContext()).recipeDao()
        val repository = RecipeRepository(recipeDao)
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]

        // Initialize adapter with empty list
        popularRecipesAdapter = PopularRecipesAdapter(emptyList()) { recipe ->
            val bundle = Bundle().apply {
                putInt("RECIPE_ID", recipe.id)
                putString("RECIPE_TITLE", recipe.title)
                putString("RECIPE_IMAGE", recipe.image)
            }
            findNavController().navigate(R.id.recipeDetailsFragment, bundle)
        }
        recipeImageView = view.findViewById(R.id.RandomRecipeIV)
        recyclerView = view.findViewById(R.id.recipeRV)



        setupRecyclerView()
        observePopularRecipes()
        observerRandomRecipe()
        recipeViewModel.getRandomRecipe()
        onRandomRecipeClick(view)

    }
    private fun setupRecyclerView() {
        recyclerView = view?.findViewById(R.id.recipeRV) ?: return
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = popularRecipesAdapter // Set adapter here
        }
    }

    private fun observePopularRecipes() {
        recipeViewModel.popularRecipes.observe(viewLifecycleOwner) { recipes ->
            popularRecipesAdapter = PopularRecipesAdapter(recipes) { recipe ->
                // Handle recipe click
                val bundle = Bundle().apply {
                    putInt("RECIPE_ID", recipe.id)
                    putString("RECIPE_TITLE", recipe.title)
                    putString("RECIPE_IMAGE", recipe.image)
                }
                findNavController().navigate(R.id.recipeDetailsFragment, bundle)
            }
            recyclerView.adapter = popularRecipesAdapter
        }    }




    private fun observerRandomRecipe() {
        Log.d("DEBUG_CHECK", "Observer started!")

        recipeViewModel.observeRandomRecipeLiveData().observe(viewLifecycleOwner) { recipe ->
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
            val randomRecipe = recipeViewModel.randomRecipeLiveData.value
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
