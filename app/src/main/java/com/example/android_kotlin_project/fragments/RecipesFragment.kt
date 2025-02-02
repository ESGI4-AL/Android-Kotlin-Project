package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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


    }


private fun observerRandomRecipe() {
    recipeMvvm.observeRandomRecipeLiveData().observe(viewLifecycleOwner) { recipe ->
        recipe?.let {
            recipeImageView = view?.findViewById(R.id.RandomRecipeIV)!!


                    Glide.with(this)
                        .load(it.image)
                        .placeholder(R.drawable.placeholder_img)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(recipeImageView)


        }
    }
}


}