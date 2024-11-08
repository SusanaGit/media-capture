package com.susanafigueroa.mediacaptureapp.ui

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.susanafigueroa.mediacaptureapp.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private data class CameraUseCases(
    val imageCapture: ImageCapture,
    val videoCapture: VideoCapture<Recorder>
)

@Composable
fun CameraScreen(
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        AndroidView(
            factory = {contextFactory ->
                val previewView = PreviewView(contextFactory)

                coroutineScope.launch {
                    val cameraProvider = startCamera(context, previewView, lifecycleOwner)
                    imageCapture = cameraProvider.imageCapture
                    videoCapture = cameraProvider.videoCapture
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {

                imageCapture?.let { takePhoto(imageCapture = it, context = context) }

            }) {
                Text(stringResource(R.string.take_a_photo))
            }

            Button(onClick = {

                videoCapture?.let {

                    if (recording == null) {
                        recording = recordVideo(it, context)
                    } else {
                        recording?.stop()
                        recording = null
                    }
                }
            }) {
                if (recording == null ) {
                    Text(stringResource(R.string.record_a_video))
                } else {
                    Text(text = stringResource(R.string.stop_recorder))
                }
            }
        }
    }
}

fun recordVideo(
    videoCapture: VideoCapture<Recorder>,
    context: Context
): Recording? {
    val videoName = SimpleDateFormat(context.getString(R.string.dateformat), Locale.UK)
        .format(System.currentTimeMillis())

    val valuesVideo = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, videoName)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
        }
    }

    val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
        context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    ).setContentValues(valuesVideo).build()

    return videoCapture.output.prepareRecording(context, mediaStoreOutputOptions)
        .start(ContextCompat.getMainExecutor(context)) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    Toast.makeText(context,
                        context.getString(R.string.recording_started), Toast.LENGTH_SHORT).show()
                }
                is VideoRecordEvent.Finalize -> {
                    val msg =
                        context.getString(R.string.video_saved_to, event.outputResults.outputUri)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
}

fun takePhoto(
    imageCapture: ImageCapture?,
    context: Context
) {

    val photoName = SimpleDateFormat(context.getString(R.string.dateformat), Locale.UK)
        .format(System.currentTimeMillis())

    val valuesPhoto = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        valuesPhoto
    ).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Toast.makeText(context,
                    context.getString(R.string.photo_capture_failed, exc.message), Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val msg = context.getString(R.string.photo_capture_succeeded, output.savedUri)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                Log.d("TakePhoto", msg)
            }
        }
    )
}

private suspend fun startCamera(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner
): CameraUseCases {
    val cameraProvider = context.getCameraProvider()

    val preview = Preview.Builder().build().apply {
        setSurfaceProvider(previewView.surfaceProvider)
    }

    val imageCapture = ImageCapture.Builder().build()

    val recorder = Recorder.Builder()
        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    cameraProvider.unbindAll()

    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture, videoCapture)

    return CameraUseCases(imageCapture, videoCapture)
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