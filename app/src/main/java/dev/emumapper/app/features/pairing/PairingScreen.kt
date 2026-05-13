package dev.emumapper.app.features.pairing

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.emumapper.app.R
import dev.emumapper.app.core.input.ControllerDisplay
import dev.emumapper.app.core.input.PadKey
import dev.emumapper.app.core.input.mapKeyEvent
import dev.emumapper.app.core.pairing.ManualWriteUiState
import dev.emumapper.app.core.settings.WriteMode
import dev.emumapper.app.core.ui.components.ActionHintBar
import dev.emumapper.app.core.ui.components.ControllerHintStyle
import dev.emumapper.app.core.ui.components.EmulatorIconRail
import dev.emumapper.app.core.ui.components.HintBarState
import dev.emumapper.app.core.ui.components.rememberHintsForState
import dev.emumapper.app.data.emulators.EmulatorDetector
import dev.emumapper.app.data.ini.WriteResult
import dev.emumapper.app.features.pairing.ui.ControllerGrid
import dev.emumapper.app.features.pairing.ui.status.WriteErrorBanner
import dev.emumapper.app.features.pairing.ui.status.WriteStatusIndicator
import dev.emumapper.app.features.pairing.ui.status.WriteSuccessIcon

private fun isStartEventFromPlayer1(
    nativeEvent: KeyEvent,
    visibleControllers: List<VisibleControllerUi>
): Boolean {
    val player1 = visibleControllers.firstOrNull()?.controller ?: return false
    return nativeEvent.deviceId == player1.deviceId
}

@Composable
fun PairingScreen(
    onOpenSettings: () -> Unit,
    enabledEmulators: Set<String>,
    viewModel: PairingViewModel,
    debugLogs: Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    val noEmulatorEnabledLabel = stringResource(R.string.no_emulator_enabled)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val lastWriteResult by viewModel.lastWriteResult.collectAsState()
    val controllers by viewModel.controllers.collectAsState()
    val writeMode by viewModel.writeMode.collectAsState()
    val isCurrentConfigWritten by viewModel.isCurrentConfigWritten.collectAsState()
    val manualWriteUiState by viewModel.manualWriteUiState.collectAsState()

    val detector = remember { EmulatorDetector(context) }
    val installed = remember { detector.installedEmulators() }
    val activeInstalled = remember(installed, enabledEmulators) {
        installed.filter { enabledEmulators.contains(it.id) }
    }

    val hasEnabledEmulators = activeInstalled.isNotEmpty()
    val hasControllers = controllers.isNotEmpty()

    val hasWriteError =
        lastWriteResult is WriteResult.Failure ||
                lastWriteResult is WriteResult.PartialFailure

    val manualWriteProgress = when (val state = manualWriteUiState) {
        ManualWriteUiState.Idle -> 0f
        is ManualWriteUiState.Holding -> state.progress
        ManualWriteUiState.Writing -> 1f
        ManualWriteUiState.Success -> 1f
    }

    val manualWriteInProgress =
        manualWriteUiState is ManualWriteUiState.Holding ||
                manualWriteUiState is ManualWriteUiState.Writing

    val manualWriteCompleted =
        manualWriteUiState is ManualWriteUiState.Success ||
                isCurrentConfigWritten

    val showWriteError =
        hasControllers &&
                hasEnabledEmulators &&
                hasWriteError &&
                !manualWriteInProgress

    val controllerHintStyle = controllers.firstOrNull()?.let {
        ControllerDisplay.hintStyleFor(it.controller)
    } ?: ControllerHintStyle.GENERIC

    val hintState = when {
        !hasControllers -> HintBarState.PairingEmpty
        writeMode == WriteMode.MANUAL -> HintBarState.PairingManualWithControllers
        else -> HintBarState.PairingAutoWithControllers
    }

    val hints = rememberHintsForState(hintState)

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                val native = event.nativeKeyEvent
                val key = mapKeyEvent(native)

                if (native.keyCode == KeyEvent.KEYCODE_BUTTON_MODE) {
                    return@onPreviewKeyEvent true
                }

                when {
                    event.type == KeyEventType.KeyDown && key == PadKey.SELECT -> {
                        onOpenSettings()
                        true
                    }

                    event.type == KeyEventType.KeyDown && key == PadKey.START -> {
                        if (!hasEnabledEmulators) {
                            return@onPreviewKeyEvent true
                        }

                        if (writeMode != WriteMode.MANUAL) {
                            return@onPreviewKeyEvent true
                        }

                        if (!isStartEventFromPlayer1(native, controllers)) {
                            return@onPreviewKeyEvent true
                        }

                        viewModel.beginManualWriteHold()
                        true
                    }

                    event.type == KeyEventType.KeyUp && key == PadKey.START -> {
                        if (!hasEnabledEmulators) {
                            return@onPreviewKeyEvent true
                        }

                        if (writeMode != WriteMode.MANUAL) {
                            return@onPreviewKeyEvent true
                        }

                        if (!isStartEventFromPlayer1(native, controllers)) {
                            return@onPreviewKeyEvent true
                        }

                        viewModel.cancelManualWriteHold()
                        true
                    }

                    else -> false
                }
            }
    ) {
        if (debugLogs) {
            Text(
                text = "DEBUG ON",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 8.dp)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f),
                contentAlignment = Alignment.Center
            ) {
                EmulatorIconRail(emulators = activeInstalled)
            }

            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 700.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ControllerGrid(controllers = controllers)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.15f),
                contentAlignment = Alignment.TopCenter
            ) {
                if (hasControllers) {
                    when {
                        !hasEnabledEmulators -> {
                            Text(
                                text = noEmulatorEnabledLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                        }

                        writeMode == WriteMode.AUTO -> {
                            WriteSuccessIcon(
                                visible = isCurrentConfigWritten,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                        }

                        else -> {
                            WriteStatusIndicator(
                                progress = manualWriteProgress,
                                writing = manualWriteInProgress,
                                completed = manualWriteCompleted,
                                controllerHintStyle = controllerHintStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                            )
                        }
                    }
                }
            }

            ActionHintBar(
                hints = hints,
                controllerHintStyle = controllerHintStyle
            )
        }

        WriteErrorBanner(
            result = lastWriteResult,
            visible = showWriteError,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}