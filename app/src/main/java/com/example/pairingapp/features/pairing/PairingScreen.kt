package com.example.pairingapp.features.pairing

import android.view.KeyEvent
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pairingapp.R
import com.example.pairingapp.core.input.ControllerDisplay
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.ui.components.ActionHintBar
import com.example.pairingapp.core.ui.components.ControllerHintStyle
import com.example.pairingapp.core.ui.components.EmulatorIconRail
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.core.ui.components.rememberHintsForState
import com.example.pairingapp.data.emulators.EmulatorDetector
import com.example.pairingapp.features.pairing.ui.ControllerGrid
import com.example.pairingapp.features.pairing.ui.status.WriteStatusIndicator
import com.example.pairingapp.features.pairing.ui.status.WriteSuccessIcon


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

    val controllers by viewModel.controllers.collectAsState()
    val manualWriteHoldProgress by viewModel.manualWriteHoldProgress.collectAsState()
    val writeMode by viewModel.writeMode.collectAsState()
    val isCurrentConfigWritten by viewModel.isCurrentConfigWritten.collectAsState()

    var manualWritePressed by remember { mutableStateOf(false) }

    val detector = remember { EmulatorDetector(context) }
    val installed = remember { detector.installedEmulators() }
    val activeInstalled = remember(installed, enabledEmulators) {
        installed.filter { enabledEmulators.contains(it.id) }
    }

    val hasEnabledEmulators = activeInstalled.isNotEmpty()
    val hasControllers = controllers.isNotEmpty()

    LaunchedEffect(hasControllers, hasEnabledEmulators, writeMode) {
        if (!hasControllers || !hasEnabledEmulators || writeMode == WriteMode.AUTO) {
            manualWritePressed = false
        }
    }

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

                        if (!isStartEventFromPlayer1(native, controllers)) {
                            return@onPreviewKeyEvent true
                        }

                        if (!manualWritePressed) {
                            manualWritePressed = true
                            viewModel.beginManualWriteHold()
                        }

                        true
                    }

                    event.type == KeyEventType.KeyUp && key == PadKey.START -> {
                        if (!hasEnabledEmulators) {
                            return@onPreviewKeyEvent true
                        }

                        if (!isStartEventFromPlayer1(native, controllers)) {
                            return@onPreviewKeyEvent true
                        }

                        manualWritePressed = false
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
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        writeMode == WriteMode.AUTO -> {
                            WriteSuccessIcon(
                                visible = isCurrentConfigWritten,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        else -> {
                            WriteStatusIndicator(
                                progress = manualWriteHoldProgress,
                                writing = manualWritePressed || manualWriteHoldProgress > 0f,
                                completed = isCurrentConfigWritten,
                                controllerHintStyle = controllerHintStyle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
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
    }
}