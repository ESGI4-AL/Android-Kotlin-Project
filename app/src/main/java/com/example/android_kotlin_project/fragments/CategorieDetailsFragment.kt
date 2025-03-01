package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.adapters.PopularRecipesAdapter
import com.example.android_kotlin_project.dataBase.MyDatabase
import com.example.android_kotlin_project.repositories.RecipeRepository
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModel
import com.example.android_kotlin_project.ui.viewmodel.RecipeViewModelFactory

class CategorieDetailsFragment : Fragment()
{
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var adapter: PopularRecipesAdapter

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    return inflater.inflate(R.layout.fragment_categorie_details, container, false)
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initViewModel()
    setupRecyclerView(view)
    observeViewModelData()
    handleCategoryArguments()
    setupToolbar(view)



}
    private fun initViewModel(){
        val recipeDao = MyDatabase.getInstance(requireContext()).recipeDao()
        val repository = RecipeRepository(recipeDao)
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]
    }
    private fun setupRecyclerView(view: View){
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        val categoryRecyclerView = view?.findViewById<RecyclerView>(R.id.categoriesRV)
        val recyclerView = view.findViewById<RecyclerView>(R.id.categoriesRV)

        // Setup RecyclerView + adapter
        adapter = PopularRecipesAdapter(emptyList()) { clickedRecipe ->
            //  recipe details bundle
            val bundle = Bundle().apply {
                putInt("RECIPE_ID", clickedRecipe.id)
                putString("RECIPE_TITLE", clickedRecipe.title)
                putString("RECIPE_IMAGE", clickedRecipe.image)
            }
            // navigation
            findNavController().navigate(R.id.recipeDetailsFragment, bundle)
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = adapter

        categoryRecyclerView?.layoutManager = gridLayoutManager
        categoryRecyclerView?.addItemDecoration(GridSpacingItemDecoration(spacingInPixels))

    }
    private fun observeViewModelData(){
        recipeViewModel.recipesByCategoryLiveData.observe(viewLifecycleOwner) { recipes ->
            adapter.updateRecipes(recipes)
        }
    }
    private fun handleCategoryArguments(){
        val categoryName = arguments?.getString("categoryName")?.lowercase()
        if (categoryName != null) {
            // Trigger the random recipes for that category
            recipeViewModel.loadRecipesByCategory(categoryName)
            val displayName = categoryName
                ?.replace("_", " ")
                ?.split(" ")
                ?.joinToString(" ") { it.capitalize().replaceFirstChar { it.uppercaseChar() }
                }
            view?.findViewById<TextView>(R.id.category_titleTV)?.text = displayName


        }
    }
    private fun setupToolbar(view: View){
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }



}