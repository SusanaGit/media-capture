package com.susanafigueroa.mediacaptureapp.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.susanafigueroa.mediacaptureapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.lifecycle.viewmodel.compose.viewModel

private data class CameraUseCases(
    val imageCapture: ImageCapture,
    val videoCapture: VideoCapture<Recorder>
)

@Composable
fun CameraScreen(
    viewModel: MediaCaptureViewModel = viewModel()
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording by remember { mutableStateOf<Recording?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imageBitmap: ImageBitmap? = imageUri?.let { uri ->
        if (uri.toString().contains("image")) {
            obtainBitmapFromUri(context, uri)?.asImageBitmap()
        } else {
            obtainBitmapFromVideoUri(context,uri)?.asImageBitmap()
        }
    }

    LaunchedEffect(imageUri) {
        imageUri?.let {
            delay(2000)
            imageUri = null
        }
    }

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

        imageBitmap?.let { bitmap ->

            Image(
                bitmap = bitmap,
                contentDescription = stringResource(R.string.photo_thumbnail),
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {

                imageCapture?.let {
                    takePhoto(imageCapture = it, context = context) { uri ->
                        uri?.let {
                            imageUri = uri
                            //listMedia = listMedia + uri
                            viewModel.addMediaItem(uri)
                        }
                    }
                }

            }) {
                Text(stringResource(R.string.take_a_photo))
            }

            Button(onClick = {

                videoCapture?.let {

                    if (recording == null) {
                        recording = recordVideo(it, context) { uri ->
                            uri?.let {
                                imageUri = uri
                                //listMedia = listMedia + uri
                                viewModel.addMediaItem(uri)
                            }
                        }
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

fun obtainBitmapFromVideoUri(
    context: Context,
    uri: Uri
): Bitmap? {
    var bitmapVideoFrame: Bitmap? = null
    val dataVideo = MediaMetadataRetriever()
    try {
        dataVideo.setDataSource(context, uri)
        bitmapVideoFrame = dataVideo.getFrameAtTime(0)

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        dataVideo.release()
    }
    return bitmapVideoFrame
}

fun obtainBitmapFromUri(
    context: Context,
    uri: Uri
): Bitmap? {
    var bitmapPhoto: Bitmap? = null
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        bitmapPhoto = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val matrix = Matrix().apply { postRotate(90f) }
        bitmapPhoto = Bitmap.createBitmap(bitmapPhoto, 0, 0, bitmapPhoto.width, bitmapPhoto.height, matrix, true)

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return bitmapPhoto
}

fun recordVideo(
    videoCapture: VideoCapture<Recorder>,
    context: Context,
    onVideoCapture: (Uri?) -> Unit
): Recording? {
    val videoName = SimpleDateFormat(context.getString(R.string.dateformat), Locale.UK)
        .format(System.currentTimeMillis())

    val valuesVideo = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, videoName)
        put(MediaStore.MediaColumns.MIME_TYPE, context.getString(R.string.video_mp4))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Video.Media.RELATIVE_PATH,
                context.getString(R.string.movies_camerax_video))
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
                    onVideoCapture(event.outputResults.outputUri)
                    Toast.makeText(context,
                        context.getString(R.string.video_recorded), Toast.LENGTH_SHORT).show()
                }
            }
        }
}

fun takePhoto(
    imageCapture: ImageCapture?,
    context: Context,
    onImageCaptured: (Uri?) -> Unit
) {

    val photoName = SimpleDateFormat(context.getString(R.string.dateformat), Locale.UK)
        .format(System.currentTimeMillis())

    val valuesPhoto = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, photoName)
        put(MediaStore.MediaColumns.MIME_TYPE, context.getString(R.string.image_jpeg))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH,
                context.getString(R.string.pictures_camerax_image))
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
                    context.getString(R.string.photo_capture_failed), Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(output.savedUri)
                val msg = context.getString(R.string.photo_capture_succeeded)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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