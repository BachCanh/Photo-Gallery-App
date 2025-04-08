package com.example.photogallery


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photogallery.ui.theme.PhotoGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhotoGalleryApp()
                }
            }
        }
    }
}

@Composable
fun PhotoGalleryApp() {
    val navController = rememberNavController()
    val photoViewModel = remember { PhotoViewModel() }

    NavHost(navController = navController, startDestination = "grid") {
        composable("grid") {
            PhotoGrid(
                photos = photoViewModel.photos,
                onPhotoClick = { photoId ->
                    navController.navigate("photo/$photoId")
                },
                onAddPhotoFromUri = { uri ->
                    photoViewModel.addPhotoFromUri(uri)
                },
                onLongPress = { photoId ->
                    photoViewModel.toggleFavorite(photoId)
                }
            )
        }
        composable(
            "photo/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getInt("photoId") ?: 0
            val photos = photoViewModel.photos
            val currentIndex = photos.indexOfFirst { it.id == photoId }.takeIf { it >= 0 } ?: 0

            FullPhotoView(
                photos = photos,
                initialIndex = currentIndex,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
