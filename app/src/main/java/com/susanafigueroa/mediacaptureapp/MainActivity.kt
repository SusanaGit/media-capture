package com.susanafigueroa.mediacaptureapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.susanafigueroa.mediacaptureapp.ui.theme.MediaCaptureAppTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaCaptureAppTheme {
                PermissionHandler()
            }
        }
    }

}

@Composable
fun PermissionHandler() {
    val context = LocalContext.current
    val requiredPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    fun checkValuePermissions(): Boolean {
        return requiredPermissions.all {permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    var allPermissionsTrue by remember { mutableStateOf(checkValuePermissions()) }
    val launcherForPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            allPermissionsTrue = permissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        if(!allPermissionsTrue) {
            launcherForPermissions.launch(requiredPermissions.toTypedArray())
        }
    }

    if (allPermissionsTrue) {
        MediaCaptureApp()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.accept_the_permissions_to_continue)
            )
        }
    }
}

