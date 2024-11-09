package com.susanafigueroa.mediacaptureapp.ui

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
) {
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("uri")
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