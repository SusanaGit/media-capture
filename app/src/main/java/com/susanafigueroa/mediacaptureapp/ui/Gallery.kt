package com.susanafigueroa.mediacaptureapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun GalleryScreen(
    viewModel: MediaCaptureViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(horizontal = 4.dp)
        ) {
            itemsIndexed(uiState.mediaListThumbnail) {index, thumbnail ->
                thumbnail.let{
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
    }
}
