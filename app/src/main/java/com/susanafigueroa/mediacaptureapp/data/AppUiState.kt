package com.susanafigueroa.mediacaptureapp.data

import android.net.Uri

data class AppUiState (
    val mediaList: List<Uri> = emptyList()
)