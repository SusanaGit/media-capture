package com.susanafigueroa.mediacaptureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.susanafigueroa.mediacaptureapp.ui.theme.MediaCaptureAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaCaptureAppTheme {
                MediaCaptureApp()
            }
        }
    }
}