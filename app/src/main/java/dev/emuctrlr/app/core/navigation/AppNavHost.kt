package dev.emuctrlr.app.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.emuctrlr.app.core.app.appGraph
import dev.emuctrlr.app.core.settings.AppLanguage
import dev.emuctrlr.app.core.settings.WriteMode
import dev.emuctrlr.app.core.utils.AppLogger
import dev.emuctrlr.app.core.utils.LogTags
import dev.emuctrlr.app.features.onboarding.emulators.OnboardingEmulatorsSetupScreen
import dev.emuctrlr.app.features.onboarding.internalcontrollers.InternalControllersSetupScreen
import dev.emuctrlr.app.features.pairing.PairingScreen
import dev.emuctrlr.app.features.pairing.PairingViewModel
import dev.emuctrlr.app.features.pairing.PairingViewModelFactory
import dev.emuctrlr.app.features.settings.SettingsHomeScreen

@Composable
fun AppNavHost(
    darkTheme: Boolean,
    onSetDarkTheme: (Boolean) -> Unit,
    language: AppLanguage,
    onSetLanguage: (AppLanguage) -> Unit,
    enabledEmulators: Set<String>,
    onSetEnabledEmulators: (Set<String>) -> Unit,
    onSetInternalController: (String?) -> Unit,
    writeMode: WriteMode,
    onSetWriteMode: (WriteMode) -> Unit,
    onboardingDone: Boolean,
    internalController: String?,
    debugLogs: Boolean,
    onSetDebugLogs: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
    onMarkOnboardingDone: () -> Unit,
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
                initialInternalController = internalController,
                onDone = { selectedInternalController ->
                    onSetInternalController(selectedInternalController)
                    navController.navigate(Routes.OnboardingEmulatorsSetup)
                }
            )
        }

        composable(Routes.OnboardingEmulatorsSetup) {
            OnboardingEmulatorsSetupScreen(
                enabledEmulators = enabledEmulators,
                onSetEnabledEmulators = onSetEnabledEmulators,
                onBack = {
                    navController.popBackStack(Routes.InternalControllersSetup, false)
                },
                onDone = {
                    onMarkOnboardingDone()
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
                internalController = internalController,
                onSetInternalController = onSetInternalController,
                debugLogs = debugLogs,
                onSetDebugLogs = onSetDebugLogs,
                onClearLogs = onClearLogs,
            )
        }
    }
}
