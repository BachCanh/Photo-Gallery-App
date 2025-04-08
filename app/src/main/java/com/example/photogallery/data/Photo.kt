package com.example.photogallery

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class Photo(
    val id: Int,
    val url: String = "",
    val uri: Uri? = null,
    val title: String = "",
    val isFavorite: Boolean = false,
    val isLocalUri: Boolean = false
)

