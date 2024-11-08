package com.susanafigueroa.mediacaptureapp.data

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class AppUiState (
    val mediaList: List<Uri> = emptyList(),
    val mediaListThumbnail: List<ImageBitmap> = emptyList()
)