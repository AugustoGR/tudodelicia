package com.br.tudodelicia

import android.graphics.Bitmap

data class Recipe(
    val title: String?,
    val description: String?,
    val imageResourceId: Bitmap,
    val id: String,
    var likedByUsers: MutableList<String>,
    var likeCount: Int,
    var commentsCount: Int
)
