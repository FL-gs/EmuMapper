package com.example.pairingapp.features.onboarding.emulators

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pairingapp.R
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.components.ActionButton
import com.example.pairingapp.core.ui.components.AppConfirmDialog
import com.example.pairingapp.data.emulators.EmulatorDef
import com.example.pairingapp.data.emulators.EmulatorDetector
import com.example.pairingapp.features.settings.SettingsViewModel
import com.example.pairingapp.features.emulators.components.EmulatorToggleList

@Composable
fun OnboardingEmulatorsSetupScreen(
    enabledEmulators: Set<String>,
    onSetEnabledEmulators: (Set<String>) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val detector = remember { EmulatorDetector(context) }

    val settingsViewModel: SettingsViewModel = viewModel()
    val uiState by settingsViewModel.uiState.collectAsState()

    var installed by remember {
        mutableStateOf<List<EmulatorDef>>(emptyList())
    }

    var focusedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val rootFocusRequester = remember { FocusRequester() }
    val backFocusRequester = remember { FocusRequester() }
    val finishFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        installed = detector.installedEmulators()
        rootFocusRequester.requestFocus()
    }

    val finishIndex = installed.size
    val backIndex = installed.size + 1

    var showSkipEmulatorsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    fun goNext() {
        if (enabledEmulators.isEmpty()) {
            showSkipEmulatorsDialog = true
        } else {
            onDone()
        }
    }

    LaunchedEffect(installed) {
        focusedIndex = focusedIndex.coerceIn(0, backIndex)
    }

    fun toggleEmulator(emulatorId: String) {
        val isEnabled = enabledEmulators.contains(emulatorId)

        if (isEnabled) {
            onSetEnabledEmulators(enabledEmulators - emulatorId)
            return
        }

        if (emulatorId == "retroarch") {
            settingsViewModel.onRetroArchToggleRequested(
                packageName = emulatorId,
                enabledEmulators = enabledEmulators,
                onSetEnabledEmulators = onSetEnabledEmulators
            )
            return
        }

        onSetEnabledEmulators(enabledEmulators + emulatorId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(rootFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (uiState.showRetroArchDialog || showSkipEmulatorsDialog) {
                    return@onPreviewKeyEvent false
                }

                if (event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                when (mapKeyEvent(event.nativeKeyEvent)) {
                    PadKey.UP -> {
                        focusedIndex = when {
                            focusedIndex == finishIndex || focusedIndex == backIndex -> {
                                (installed.size - 1).coerceAtLeast(0)
                            }

                            focusedIndex > 0 -> {
                                focusedIndex - 1
                            }

                            else -> {
                                focusedIndex
                            }
                        }
                        true
                    }

                    PadKey.DOWN -> {
                        focusedIndex = if (focusedIndex < installed.size - 1) {
                            focusedIndex + 1
                        } else {
                            finishIndex
                        }
                        true
                    }

                    PadKey.LEFT,
                    PadKey.RIGHT -> {
                        focusedIndex = when (focusedIndex) {
                            finishIndex -> backIndex
                            backIndex -> finishIndex
                            else -> focusedIndex
                        }
                        true
                    }

                    PadKey.A -> {
                        val emulator = installed.getOrNull(focusedIndex)

                        when {
                            emulator != null -> toggleEmulator(emulator.id)
                            focusedIndex == finishIndex -> goNext()
                            focusedIndex == backIndex -> onBack()
                        }

                        true
                    }

                    PadKey.B -> {
                        onBack()
                        true
                    }

                    else -> false
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 88.dp
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_emulators_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = stringResource(R.string.onboarding_emulators_explanation),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                }

                Spacer(modifier = Modifier.height(64.dp))

                EmulatorToggleList(
                    installed = installed,
                    enabledEmulators = enabledEmulators,
                    focusedIndex = focusedIndex,
                )

                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = stringResource(R.string.onboarding_emulators_settings_hint),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center
                )
            }
        }

        ActionButton(
            text = stringResource(R.string.hint_back),
            selected = focusedIndex == backIndex,
            active = true,
            focusRequester = backFocusRequester,
            focused = focusedIndex == backIndex,
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
                .widthIn(min = 120.dp, max = 160.dp)
        )

        ActionButton(
            text = stringResource(R.string.finish),
            selected = focusedIndex == finishIndex,
            active = true,
            focusRequester = finishFocusRequester,
            focused = focusedIndex == finishIndex,
            onClick = {
                goNext()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .widthIn(min = 120.dp, max = 160.dp)
        )
    }

    if (uiState.showRetroArchDialog) {
        AlertDialog(
            onDismissRequest = {
                settingsViewModel.dismissRetroArchDialog()
            },
            title = {
                Text(stringResource(R.string.retroarch_dialog_title))
            },
            text = {
                Text(stringResource(R.string.retroarch_dialog_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.confirmRetroArchSetup(
                            enabledEmulators = enabledEmulators,
                            onSetEnabledEmulators = onSetEnabledEmulators
                        )
                    }
                ) {
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.dismissRetroArchDialog()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showSkipEmulatorsDialog) {
        AppConfirmDialog(
            title = stringResource(R.string.skip_emulators_title),
            message = stringResource(R.string.skip_emulators_message),
            confirmText = stringResource(R.string.continue_without_emulators),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                showSkipEmulatorsDialog = false
                onDone()
            },
            onDismiss = {
                showSkipEmulatorsDialog = false
            }
        )
    }
}