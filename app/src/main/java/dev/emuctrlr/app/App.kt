package dev.emuctrlr.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.emuctrlr.app.core.app.AppViewModel
import dev.emuctrlr.app.core.app.AppViewModelFactory
import dev.emuctrlr.app.core.app.appGraph
import dev.emuctrlr.app.core.navigation.AppNavHost
import dev.emuctrlr.app.core.pairing.AutoPairingService
import dev.emuctrlr.app.core.settings.WriteMode
import dev.emuctrlr.app.core.settings.localized
import dev.emuctrlr.app.core.ui.theme.AppTheme

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
    val visibleControllers by pairingEngine.visibleControllers.collectAsState()

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
            AutoPairingService.Companion.start(context)
        } else {
            AutoPairingService.Companion.stop(context)
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
                    internalController = s.internalController,
                    onSetInternalController = viewModel::setInternalController,
                    visibleControllers = visibleControllers,
                    controllerMappingOverrides = s.controllerMappingOverrides,
                    onSetControllerMappingBinding = viewModel::setControllerMappingBinding,
                    onResetControllerMapping = viewModel::resetControllerMapping,
                    debugLogs = s.debugLogs,
                    onSetDebugLogs = viewModel::setDebugLogs,
                    onClearLogs = viewModel::clearLogs,
                    onMarkOnboardingDone = viewModel::markOnboardingDone,
                )
            }
        }
    }
}
