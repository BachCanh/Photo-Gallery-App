package com.example.photogallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullPhotoView(
    photos: List<Photo>,
    initialIndex: Int,
    onBackClick: () -> Unit
) {
    // Remember the pager state with initial index and total pages
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // The Horizontal Pager to swipe between photos
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ZoomablePhoto(photo = photos[page])
        }

        // A top-left back button with a semi-transparent circular background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        shape = CircleShape
                    )
                    .padding(8.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Bottom overlay for Previous/Next navigation buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            // Previous button (shows only if not on the first page)
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Next button (shows only if not on the last page)
            if (pagerState.currentPage < photos.size - 1) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                            shape = CircleShape
                        )
                        .padding(8.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ZoomablePhoto(photo: Photo) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(model = photo.url),
            contentDescription = photo.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Update scale with constraints
                        scale = (scale * zoom).coerceIn(1f, 3f)

                        // Calculate the maximum allowed translation values based on scale
                        val maxX = (scale - 1) * size.width / 2
                        val maxY = (scale - 1) * size.height / 2

                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                    }
                }
        )
    }
}
