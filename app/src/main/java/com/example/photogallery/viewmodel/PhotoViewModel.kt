package com.example.photogallery

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PhotoViewModel : ViewModel() {
    // Using mutableStateListOf for automatic recomposition when the list changes
    private val _photos = mutableStateListOf<Photo>()
    val photos: List<Photo> get() = _photos

    init {
        // Add some sample photos
        addSamplePhotos()
    }

    private fun addSamplePhotos() {
        val sampleUrls = listOf(
            "https://picsum.photos/id/10/800/800",
            "https://picsum.photos/id/11/800/800",
            "https://picsum.photos/id/12/800/800",
            "https://picsum.photos/id/13/800/800",
            "https://picsum.photos/id/14/800/800",
            "https://picsum.photos/id/15/800/800",
            "https://picsum.photos/id/16/800/800",
            "https://picsum.photos/id/17/800/800"
        )

        sampleUrls.forEachIndexed { index, url ->
            _photos.add(Photo(id = index, url = url, title = "Photo $index"))
        }
    }

    fun addSamplePhoto() {
        val newId = if (_photos.isEmpty()) 0 else _photos.maxOf { it.id } + 1
        val randomId = (100..999).random()
        _photos.add(
            Photo(
                id = newId,
                url = "https://picsum.photos/id/$randomId/800/800",
                title = "New Photo $newId"
            )
        )
    }
    fun addPhotoFromUri(uri: Uri) {
        val newId = if (_photos.isEmpty()) 0 else _photos.maxOf { it.id } + 1
        _photos.add(
            Photo(
                id = newId,
                uri = uri,
                title = "Photo $newId",
                isLocalUri = true
            )
        )
    }


    fun toggleFavorite(photoId: Int) {
        val index = _photos.indexOfFirst { it.id == photoId }
        if (index >= 0) {
            val photo = _photos[index]
            _photos[index] = photo.copy(isFavorite = !photo.isFavorite)
        }
    }

    fun deletePhoto(photoId: Int) {
        _photos.removeIf { it.id == photoId }
    }
}
