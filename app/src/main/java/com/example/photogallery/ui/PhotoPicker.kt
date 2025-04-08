package com.example.photogallery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EnhancedFAB(onPhotoAdded: (Uri) -> Unit) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d("EnhancedFAB", "Camera launcher success: $success, URI: $tempCameraUri")

        if (success && tempCameraUri != null) {
            onPhotoAdded(tempCameraUri!!)
        } else {
            Log.e("EnhancedFAB", "Failed to capture photo or invalid URI!")
        }
    }

    // For camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempCameraUri = createImageFileUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onPhotoAdded(it) }
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = { showMenu = true }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Photo")
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Take Photo") },
                leadingIcon = { Icon(Icons.Default.Camera, "Camera") },
                onClick = {
                    showMenu = false
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED) {
                        tempCameraUri = createImageFileUri(context)
                        tempCameraUri?.let { cameraLauncher.launch(it) }
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )

            DropdownMenuItem(
                text = { Text("Choose from Gallery") },
                leadingIcon = { Icon(Icons.Filled.PhotoLibrary, "Gallery") },
                onClick = {
                    showMenu = false
                    galleryLauncher.launch("image/*")
                }
            )
        }
    }
}

fun createImageFileUri(context: Context): Uri? {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg",              /* suffix */
        storageDir           /* directory */
    )
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
}

