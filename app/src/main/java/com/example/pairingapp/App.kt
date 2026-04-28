package com.example.pairingapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pairingapp.core.app.AppViewModel
import com.example.pairingapp.core.app.AppViewModelFactory
import com.example.pairingapp.core.app.appGraph
import com.example.pairingapp.core.navigation.AppNavHost
import com.example.pairingapp.core.pairing.AutoPairingService
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.ui.theme.AppTheme
import androidx.compose.runtime.CompositionLocalProvider
import com.example.pairingapp.core.settings.localized

@Composable
fun App() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val appGraph = remember { context.appGraph }
    val pairingEngine = remember { appGraph.pairingEngine }

    val viewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(appGraph.settingsRepository)
    )

    val settings by viewModel.settings.collectAsState(initial = null)

    if (settings == null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) { }
        return
    }

    val s = settings!!

    val localizedContext = remember(context, s.language) {
        context.localized(s.language)
    }

    DisposableEffect(lifecycleOwner, pairingEngine) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            pairingEngine.attachHost("ui")
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> pairingEngine.attachHost("ui")
                Lifecycle.Event.ON_STOP -> pairingEngine.detachHost("ui")
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            pairingEngine.detachHost("ui")
        }
    }

    LaunchedEffect(s.writeMode) {
        if (s.writeMode == WriteMode.AUTO) {
            AutoPairingService.start(context)
        } else {
            AutoPairingService.stop(context)
        }
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        AppTheme(darkTheme = s.darkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavHost(
                    darkTheme = s.darkTheme,
                    onSetDarkTheme = viewModel::setDarkTheme,
                    language = s.language,
                    onSetLanguage = viewModel::setLanguage,
                    enabledEmulators = s.enabledEmulators,
                    onSetEnabledEmulators = viewModel::setEnabledEmulators,
                    writeMode = s.writeMode,
                    onSetWriteMode = viewModel::setWriteMode,
                    onboardingDone = s.onboardingDone,
                    internalController1 = s.internalController1,
                    internalController2 = s.internalController2,
                    onSetInternalControllers = viewModel::setInternalControllers,
                    debugLogs = s.debugLogs,
                    onSetDebugLogs = viewModel::setDebugLogs,
                    onClearLogs = viewModel::clearLogs,
                    onMarkOnboardingDone = viewModel::markOnboardingDone,
                )
            }
        }
    }
}
