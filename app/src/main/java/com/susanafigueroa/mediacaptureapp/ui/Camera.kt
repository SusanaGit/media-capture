package com.susanafigueroa.mediacaptureapp.ui

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.susanafigueroa.mediacaptureapp.R
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {

            }) {
                Text(stringResource(R.string.take_a_photo))
            }

            Button(onClick = {

            }) {
                Text(stringResource(R.string.record_a_video))
            }

        }
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