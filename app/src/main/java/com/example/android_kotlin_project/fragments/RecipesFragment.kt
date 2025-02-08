package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

        recipeImageView =view.findViewById(R.id.RandomRecipeIV)

        val recipeDao =MyDatabase.getInstance(requireContext()).recipeDao()
        val repository =RecipeRepository(recipeDao)
        val viewModelFactory =RecipeViewModelFactory(repository)
        recipeMvvm = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]

        recipeMvvm.getRandomRecipe()
        observerRandomRecipe()

        onRandomRecipeClick(view)



    }

//    private fun observerRandomRecipe() {
//        recipeMvvm.observeRandomRecipeLiveData().observe(viewLifecycleOwner, Observer { recipe ->
//            if (recipe != null) {
//                val imageUrl = recipe.image
//                Log.d("RecipeFragment", "Recipe Image URL: $imageUrl")
//
//                // Load Image with Glide
//                Glide.with(this)
//                    .load(imageUrl ?: R.drawable.placeholder_img)
//                    .placeholder(R.drawable.placeholder_img)
//                    .error(R.drawable.placeholder_img) // Show placeholder if image is missing
//                    .transition(DrawableTransitionOptions.withCrossFade(500))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(recipeImageView)
//            } else {
//                Log.e("RecipeFragment", "API returned null recipe")
//                Toast.makeText(requireContext(), "Failed to load recipe", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//
//    }
private fun observerRandomRecipe() {
    recipeMvvm.observeRandomRecipeLiveData().observe(viewLifecycleOwner) { recipe ->
        recipe?.let {
            val imageUrl = it.image
            Log.d("GLIDE_DEBUG", "Loading Image URL: $imageUrl")

            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_img)
                .error(R.drawable.placeholder_img)  // Show error image if URL fails
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(recipeImageView)
        } ?: Log.e("GLIDE_ERROR", "Recipe data is null")
    }
}


    private fun onRandomRecipeClick(view: View) {
        val recipeCard = view.findViewById<View>(R.id.RandomRecipeIV)

        recipeCard.setOnClickListener {
            val randomRecipe =recipeMvvm.observeRandomRecipeLiveData().value
            // val fragment = RecipeDetailsFragment()

            if(randomRecipe != null){
                val bundle = Bundle().apply {
                    putInt("RECIPE_ID", randomRecipe.id)
                    putString("RECIPE_TITLE", randomRecipe.title)
                    putString("RECIPE_IMAGE", randomRecipe.image)
                }

//                requireActivity().supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .addToBackStack(null)
//                    .commit()
                findNavController().navigate(R.id.recipeDetailsFragment, bundle)
            }


//            val bundle = Bundle()
//            bundle.putString("id", "4517")
//            fragment.arguments = bundle


//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container , fragment)
//                .commit()


        }
    }








}