package com.example.pairingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.example.pairingapp.core.utils.AppLogger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DebugInfos()
            App()
        }
    }
}

@Composable
fun DebugInfos() {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        AppLogger.d("MY_DEBUG", "density=${density.density}")
        AppLogger.d("MY_DEBUG", "fontScale=${density.fontScale}")
        AppLogger.d("MY_DEBUG", "screenWidthDp=${configuration.screenWidthDp}")
    }
}
