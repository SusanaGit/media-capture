package com.susanafigueroa.mediacaptureapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.susanafigueroa.mediacaptureapp.R

@Composable
fun PlayerScreen(
    viewModel: MediaCaptureViewModel
) {

    val uiState by viewModel.uiState.collectAsState()
    val mediaSelectedUri = uiState.mediaSelectedUri

    if (mediaSelectedUri.toString().contains(stringResource(R.string.image))) {

        Image(
            painter = rememberAsyncImagePainter(mediaSelectedUri),
            contentDescription = stringResource(R.string.selected_image),
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

    } else if (mediaSelectedUri.toString().contains(stringResource(R.string.video))){

        val context = LocalContext.current

        val player = remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(mediaSelectedUri.toString())
                setMediaItem(mediaItem)
                playWhenReady = true
                prepare()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { contextPlayer ->
                    StyledPlayerView(contextPlayer).apply {
                        this.player = player
                        useController = true
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}