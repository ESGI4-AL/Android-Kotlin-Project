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
import com.example.android_kotlin_project.models.Recipe
import com.example.android_kotlin_project.models.RecipeList
import com.example.android_kotlin_project.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [RecipesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecipesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }
private lateinit var recipeImageView: ImageView

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    // all the population of the elements should happen in  sperate functions that are in repos and serices and dto da and shit
        recipeImageView = view.findViewById(R.id.RandomRecipeIV)

        RetrofitInstance.api.getRandomRecipe().enqueue(object:Callback<RecipeList>{
            override fun onResponse(call: Call<RecipeList>, response: Response<RecipeList>) {
                if (response.body()!= null){
                    val randomRecipe: Recipe= response.body()!!.recipes[0];
                    Log.d("TEST", "recipe Id ${randomRecipe.id} name ${randomRecipe.title}")
                    Glide.with(this@RecipesFragment)
                        .load(randomRecipe.image)
                        .placeholder(R.drawable.placeholder_img)
                        .into(recipeImageView)

                }else {
                    return
                }
            }

            override fun onFailure(call: Call<RecipeList>, t: Throwable) {
                Log.d("Recipe fragment" , t.message.toString())
            }
        })
    }
}