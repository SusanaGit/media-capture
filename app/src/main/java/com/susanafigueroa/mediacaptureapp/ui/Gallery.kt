package com.susanafigueroa.mediacaptureapp.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GalleryScreen(
    viewModel: MediaCaptureViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsState()

    var mediaSelectedUri by remember { mutableStateOf<Uri?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(horizontal = 4.dp)
        ) {
            itemsIndexed(uiState.mediaListThumbnail) {index, thumbnail ->

                Image(
                    bitmap = thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp)
                        .padding(4.dp)
                        .clickable {
                            mediaSelectedUri = uiState.mediaList[index]
                            mediaSelectedUri?.let{uri ->
                                viewModel.setMediaSelectedUri(uri)
                                navController.navigate("Player")
                            }
                        },
                    contentScale = ContentScale.Crop
                )
            }
    }
}
