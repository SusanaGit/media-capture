package com.susanafigueroa.mediacaptureapp.ui

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
) {
    Column(
    ) {

    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also {cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            },
            ContextCompat.getMainExecutor(this))
        }
    }