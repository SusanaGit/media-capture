package com.susanafigueroa.mediacaptureapp

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

enum class MediaCaptureScreen(@StringRes val title: Int) {
    Gallery(title = R.string.gallery),
    Camera(title = R.string.camera),
    Player(title = R.string.player)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCaptureAppBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
    )
}
@Composable
fun MediaCaptureApp(
) {
    Scaffold(
        topBar = {
            MediaCaptureAppBar() }
    ) { innerPadding ->
    }
}