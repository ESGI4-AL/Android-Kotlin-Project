package com.example.android_kotlin_project.data

import android.util.Log
import com.example.android_kotlin_project.api.RetrofitClient
import com.example.android_kotlin_project.models.NewsItem
import com.example.android_kotlin_project.models.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NewsRepository {

    fun fetchNews(callback: (List<NewsItem>) -> Unit) {
        RetrofitClient.instance.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.articles?.map {
                        NewsItem(
                            title = it.title,
                            description = it.description ?: "",
                            image = it.urlToImage ?: "",
                            link = it.url
                        )
                    } ?: emptyList()
                    callback(articles)
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("NewsRepository", "Erreur lors de la récupération des actualités", t)
                callback(emptyList())
            }
        })
    }
}
