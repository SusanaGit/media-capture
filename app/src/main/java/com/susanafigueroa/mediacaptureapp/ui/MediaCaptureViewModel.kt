package com.susanafigueroa.mediacaptureapp.ui

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.susanafigueroa.mediacaptureapp.data.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaCaptureViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun addMediaItem(newMediaItem: Uri) {
        _uiState.value = uiState.value.copy(
            mediaList = uiState.value.mediaList + newMediaItem
        )
    }

    fun addMediaItemThumbnail(newMediaItemThumbnail: ImageBitmap) {
        _uiState.value = uiState.value.copy(
            mediaListThumbnail = uiState.value.mediaListThumbnail + newMediaItemThumbnail
        )
    }

    fun setMediaSelectedUri(mediaSelectedUri: Uri) {
        _uiState.value = uiState.value.copy(
            mediaSelectedUri = mediaSelectedUri
        )
    }
}