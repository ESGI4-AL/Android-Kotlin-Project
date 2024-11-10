package com.example.android_kotlin_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.models.SportNews

class ItemAdapterNewsSport(private val itemList: List<SportNews>) : RecyclerView.Adapter<ItemAdapterNewsSport.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        val subtitleTextView: TextView = itemView.findViewById(R.id.itemSubtitle)
        val imageImageView: ImageView = itemView.findViewById(R.id.imageSport)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sport, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.title
        holder.subtitleTextView.text = item.subtitle

        val imageName = item.image
        val imageResource = holder.itemView.context.resources.getIdentifier(imageName, "drawable", holder.itemView.context.packageName)
        holder.imageImageView.setImageResource(imageResource)
    }

    override fun getItemCount(): Int = itemList.size
}
