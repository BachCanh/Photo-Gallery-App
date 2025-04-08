package com.example.photogallery

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoGrid(
    photos: List<Photo>,
    onPhotoClick: (Int) -> Unit,
    onAddPhotoFromUri: (Uri) -> Unit,
    onLongPress: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Gallery") }
            )
        },
        floatingActionButton = {
            EnhancedFAB(onPhotoAdded = onAddPhotoFromUri)
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(photos, key = { it.id }) { photo ->
                PhotoGridItem(
                    photo = photo,
                    onClick = { onPhotoClick(photo.id) },
                    onLongClick = { onLongPress(photo.id) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

// Update PhotoGridItem to handle both URL and URI

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGridItem(
    photo: Photo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val painter = if (photo.isLocalUri && photo.uri != null) {
            rememberAsyncImagePainter(photo.uri)
        } else {
            rememberAsyncImagePainter(photo.url)
        }

        Image(
            painter = painter,
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        )

        if (photo.isFavorite) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}
