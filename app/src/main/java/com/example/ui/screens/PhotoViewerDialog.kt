package com.example.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun PhotoViewerDialog(
    photos: List<String>,
    initialIndex: Int = 0,
    onDismissRequest: () -> Unit
) {
    ExpandedPhotoDialog(
        mediaList = photos,
        initialIndex = initialIndex,
        title = "Galleria Multimediale",
        onDismissRequest = onDismissRequest
    )
}
