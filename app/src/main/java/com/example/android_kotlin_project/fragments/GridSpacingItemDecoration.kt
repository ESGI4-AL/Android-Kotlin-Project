package com.example.android_kotlin_project.fragments

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration (private val spacing : Int): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: android.graphics.Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spacing
        outRect.right = spacing
        outRect.top = spacing
        outRect.bottom = spacing
    }
}