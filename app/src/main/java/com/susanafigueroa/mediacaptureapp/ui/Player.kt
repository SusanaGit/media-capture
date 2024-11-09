package com.susanafigueroa.mediacaptureapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PlayerScreen(
    viewModel: MediaCaptureViewModel
) {

    val mediaSelectedUri = viewModel.uiState.value.mediaSelectedUri

    if (mediaSelectedUri.toString().contains("image")) {

        Text(text = "image")

    } else if (mediaSelectedUri.toString().contains("video")){

        val context = LocalContext.current

        val player = remember {
            ExoPlayer.Builder(context).build().apply {

                var mediaItem = MediaItem.fromUri("")

                if (viewModel.uiState.value.mediaSelectedUri != null) {
                    mediaItem = MediaItem.fromUri(viewModel.uiState.value.mediaSelectedUri!!.toString())
                }

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