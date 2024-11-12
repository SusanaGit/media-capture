package com.susanafigueroa.mediacaptureapp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.susanafigueroa.mediacaptureapp.ui.theme.screens.CameraScreen
import com.susanafigueroa.mediacaptureapp.ui.theme.screens.GalleryScreen
import com.susanafigueroa.mediacaptureapp.ui.theme.screens.MediaCaptureViewModel
import com.susanafigueroa.mediacaptureapp.ui.theme.screens.PlayerScreen

enum class MediaCaptureScreen(@StringRes val title: Int) {
    Gallery(title = R.string.gallery),
    Camera(title = R.string.camera),
    Player(title = R.string.player)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCaptureAppBar(
    currentScreen: MediaCaptureScreen,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
    )
}

@Composable
fun MediaCaptureApp(
    navController: NavHostController = rememberNavController()
) {

    val viewModel: MediaCaptureViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MediaCaptureScreen.valueOf(
        backStackEntry?.destination?.route ?: MediaCaptureScreen.Gallery.name
    )

    Scaffold(
        topBar = {
            MediaCaptureAppBar( currentScreen = currentScreen)
        },
        bottomBar = {
            MediaCaptureBottomBar(
                navController = navController,
                currentScreen = currentScreen
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MediaCaptureScreen.Gallery.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = MediaCaptureScreen.Gallery.name) {
                GalleryScreen(viewModel, navController = navController)
            }

            composable(route = MediaCaptureScreen.Camera.name) {
                CameraScreen(viewModel)
            }

            composable(route = MediaCaptureScreen.Player.name) {
                PlayerScreen(viewModel)
            }
        }

    }
}

@Composable
fun MediaCaptureBottomBar(
    navController: NavHostController,
    currentScreen: MediaCaptureScreen
) {

    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == MediaCaptureScreen.Camera,
            onClick = {
                navController.navigate(MediaCaptureScreen.Camera.name)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_camera_24),
                    contentDescription = stringResource(id = R.string.camera)
                )
            }
        )
        NavigationBarItem(
            selected = currentScreen == MediaCaptureScreen.Gallery,
            onClick = {
                  navController.navigate(MediaCaptureScreen.Gallery.name)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_local_library_24),
                    contentDescription = stringResource(id = R.string.gallery)
                )
            }
        )
        NavigationBarItem(
            selected = currentScreen == MediaCaptureScreen.Player,
            onClick = {
                navController.navigate(MediaCaptureScreen.Player.name)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_play_circle_outline_24),
                    contentDescription = stringResource(id = R.string.player)
                )
            }
        )
    }
}
