package com.example.android_kotlin_project.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.NewsItem
import com.bumptech.glide.Glide

class NewsAdapter(private val context: Context, private val newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        val btnReadMore: Button = itemView.findViewById(R.id.btnReadMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.newsTitle.text = news.title
        holder.newsDescription.text = news.description

        Glide.with(context)
            .load(news.image)
            .placeholder(R.drawable.n1)
            .error(R.drawable.n1)
            .into(holder.newsImage)

        holder.btnReadMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link))
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = newsList.size
}
