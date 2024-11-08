package com.susanafigueroa.mediacaptureapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
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
            items(items = uiState.mediaList, key = { itemMedia -> itemMedia }) {uri ->
                Text(text = uri.toString())
            }
    }
}
