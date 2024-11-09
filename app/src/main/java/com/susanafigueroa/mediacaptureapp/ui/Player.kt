package com.susanafigueroa.mediacaptureapp.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView

@Composable
fun PlayerScreen(
    viewModel: MediaCaptureViewModel
) {
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {

            Log.d("MediaSelectedUri", viewModel.uiState.value.mediaSelectedUri.toString())

            var mediaItem = MediaItem.fromUri("")

            if (viewModel.uiState.value.mediaSelectedUri != null) {
                mediaItem = MediaItem.fromUri(viewModel.uiState.value.mediaSelectedUri!!.toString())
            }

            setMediaItem(mediaItem)
            prepare()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { contextPlayer ->
            PlayerControlView(contextPlayer).apply {
                this.player = player
            }
        })
    }
}