package com.example.pairingapp.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pairingapp.core.app.appGraph
import com.example.pairingapp.core.settings.AppLanguage
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags
import com.example.pairingapp.features.onboarding.internalcontrollers.InternalControllersSetupScreen
import com.example.pairingapp.features.pairing.PairingScreen
import com.example.pairingapp.features.pairing.PairingViewModel
import com.example.pairingapp.features.pairing.PairingViewModelFactory
import com.example.pairingapp.features.settings.SettingsHomeScreen

@Composable
fun AppNavHost(
    darkTheme: Boolean,
    onSetDarkTheme: (Boolean) -> Unit,
    language: AppLanguage,
    onSetLanguage: (AppLanguage) -> Unit,
    enabledEmulators: Set<String>,
    onSetEnabledEmulators: (Set<String>) -> Unit,
    onSetInternalControllers: (String?, String?) -> Unit,
    writeMode: WriteMode,
    onSetWriteMode: (WriteMode) -> Unit,
    onboardingDone: Boolean,
    internalController1: String?,
    internalController2: String?,
    onCompleteOnboarding: (String?, String?) -> Unit,
    debugLogs: Boolean,
    onSetDebugLogs: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
) {
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            AppLogger.d(LogTags.NAV, "destination -> ${destination.route}")
        }
    }

    val start = if (onboardingDone) Routes.Pairing else Routes.InternalControllersSetup

    NavHost(
        navController = navController,
        startDestination = start,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 80,
                    delayMillis = 400
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(durationMillis = 180)
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 220,
                    delayMillis = 180
                )
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(durationMillis = 120)
            )
        }
    ) {
        composable(Routes.InternalControllersSetup) {
            InternalControllersSetupScreen(
                initialInternal1 = internalController1,
                initialInternal2 = internalController2,
                onDone = { i1, i2 ->
                    onCompleteOnboarding(i1, i2)
                    navController.navigate(Routes.Pairing) {
                        popUpTo(Routes.InternalControllersSetup) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Pairing) {
            val context = LocalContext.current
            val pairingViewModel: PairingViewModel = viewModel(
                factory = PairingViewModelFactory(context.appGraph.pairingEngine)
            )

            PairingScreen(
                onOpenSettings = { navController.navigate(Routes.SettingsHome) },
                enabledEmulators = enabledEmulators,
                viewModel = pairingViewModel,
                debugLogs = debugLogs
            )
        }

        composable(Routes.SettingsHome) {
            SettingsHomeScreen(
                onBack = { navController.popBackStack(Routes.Pairing, false) },
                darkTheme = darkTheme,
                onSetDarkTheme = onSetDarkTheme,
                language = language,
                onSetLanguage = onSetLanguage,
                writeMode = writeMode,
                onSetWriteMode = onSetWriteMode,
                enabledEmulators = enabledEmulators,
                onSetEnabledEmulators = onSetEnabledEmulators,
                internalController1 = internalController1,
                internalController2 = internalController2,
                onSetInternalControllers = onSetInternalControllers,
                debugLogs = debugLogs,
                onSetDebugLogs = onSetDebugLogs,
                onClearLogs = onClearLogs,
            )
        }
    }
}
