package com.susanafigueroa.mediacaptureapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.susanafigueroa.mediacaptureapp.R

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
        itemsIndexed(uiState.mediaListImages) { index, image ->

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp)
                    .padding(4.dp)
                    .clickable {
                        mediaSelectedUri = uiState.mediaList[index]
                        mediaSelectedUri?.let { uri ->
                            viewModel.setMediaSelectedUri(uri)
                            navController.navigate("Player")
                        }
                    }
            ) {

                Image(
                    bitmap = image,
                    contentDescription = "image thumbnail",
                    contentScale = ContentScale.Crop
                )

                if (uiState.mediaList[index].toString().contains("image")) {

                    ShowIcon(
                        typeMedia = "image",
                        iconId = R.drawable.baseline_emoji_nature_24
                    )

                } else if (uiState.mediaList[index].toString().contains("video")) {

                    ShowIcon(
                        typeMedia = "image",
                        iconId = R.drawable.baseline_ondemand_video_24
                    )

                }
            }
        }
    }
}

@Composable
fun ShowIcon(typeMedia: String, iconId: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = typeMedia,
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.LightGray,
                            Color.Gray,
                            Color.DarkGray
                        )
                    )
                )
                .border(1.dp, Color.White),
            tint = Color.White

        )
    }
}