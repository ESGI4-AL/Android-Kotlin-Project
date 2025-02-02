package com.example.android_kotlin_project.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Us(
    val amount: Double,
    val unitLong: String,
    val unitShort: String
) : Parcelable