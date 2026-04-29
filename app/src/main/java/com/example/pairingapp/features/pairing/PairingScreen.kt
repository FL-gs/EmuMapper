package com.example.pairingapp.features.pairing

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pairingapp.R
import com.example.pairingapp.core.input.ControllerDisplay
import com.example.pairingapp.core.input.GamepadAction
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.ui.animation.rememberPulsingColor
import com.example.pairingapp.core.ui.components.ActionHintBar
import com.example.pairingapp.core.ui.components.ActionIconResolver
import com.example.pairingapp.core.ui.components.ControllerHintStyle
import com.example.pairingapp.core.ui.components.EmulatorIconRail
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.core.ui.components.ProgressBar
import com.example.pairingapp.core.ui.components.rememberHintsForState
import com.example.pairingapp.data.emulators.EmulatorDetector
import com.example.pairingapp.data.ini.WriteResult
import com.example.pairingapp.features.pairing.ui.ControllerGrid

private fun isStartEventFromPlayer1(
    nativeEvent: KeyEvent,
    visibleControllers: List<VisibleControllerUi>
): Boolean {
    val player1 = visibleControllers.firstOrNull()?.controller ?: return false
    return nativeEvent.deviceId == player1.deviceId
}

@Composable
private fun HoldStartToWriteLabel(
    controllerHintStyle: ControllerHintStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    val hold = stringResource(R.string.hold)
    val toWrite = stringResource(R.string.to_write)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = hold,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )

        Icon(
            painter = painterResource(
                id = ActionIconResolver.iconRes(
                    GamepadAction.START,
                    controllerHintStyle
                )
            ),
            contentDescription = "Start",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = toWrite,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
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

    val writeSuccessLabel = stringResource(R.string.controllers_saved)
    val noEmulatorEnabledLabel = stringResource(R.string.no_emulator_enabled)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val lastWriteResult by viewModel.lastWriteResult.collectAsState()
    val controllers by viewModel.controllers.collectAsState()
    val manualWriteHoldProgress by viewModel.manualWriteHoldProgress.collectAsState()
    val writeMode by viewModel.writeMode.collectAsState()
    val isCurrentConfigWritten by viewModel.isCurrentConfigWritten.collectAsState()
    val showSuccessFlash by viewModel.showSuccessFlash.collectAsState()

    val detector = remember { EmulatorDetector(context) }
    val installed = remember { detector.installedEmulators() }
    val activeInstalled = remember(installed, enabledEmulators) {
        installed.filter { enabledEmulators.contains(it.id) }
    }

    val hasEnabledEmulators = activeInstalled.isNotEmpty()

    val controllerHintStyle = controllers.firstOrNull()?.let {
        ControllerDisplay.hintStyleFor(it.controller)
    } ?: ControllerHintStyle.GENERIC

    val animatedHoldStartColor = rememberPulsingColor(
        enabled = writeMode != WriteMode.AUTO,
        initialColor = MaterialTheme.colorScheme.secondary,
        targetColor = MaterialTheme.colorScheme.primary,
        durationMillis = 600
    )

    val hasControllers = controllers.isNotEmpty()

    val hintState = when {
        !hasControllers -> HintBarState.PairingEmpty
        writeMode == WriteMode.MANUAL -> HintBarState.PairingManualWithControllers
        else -> HintBarState.PairingAutoWithControllers
    }

    val progressColor = when {
        showSuccessFlash -> animatedHoldStartColor
        else -> MaterialTheme.colorScheme.onSurface.copy(0.4f)
    }

    val hints = rememberHintsForState(hintState)

    val writeErrorText = when (val result = lastWriteResult) {

        is WriteResult.Failure -> {
            "${result.emulatorId}: ${result.reason}"
        }

        is WriteResult.PartialFailure -> {
            val messages = result.failures.map {
                "${it.emulatorId}: ${it.reason}"
            }

            when {
                messages.size <= 2 -> {
                    messages.joinToString(", ")
                }

                else -> {
                    val firstTwo = messages.take(2).joinToString(", ")
                    val remaining = messages.size - 2
                    "$firstTwo + $remaining more"
                }
            }
        }

        else -> null
    }

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

                        viewModel.beginManualWriteHold()
                        true
                    }

                    event.type == KeyEventType.KeyUp && key == PadKey.START -> {
                        if (!hasEnabledEmulators) {
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
                    .padding(top = 80.dp, end = 8.dp) // 👈 descend le texte
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
                contentAlignment = Alignment.Center
            ) {
                if (hasControllers && writeMode != WriteMode.AUTO) {
                    if (!hasEnabledEmulators) {
                        Text(
                            text = noEmulatorEnabledLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isCurrentConfigWritten) {
                                Text(
                                    text = writeSuccessLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                HoldStartToWriteLabel(
                                    controllerHintStyle = controllerHintStyle,
                                    color = animatedHoldStartColor
                                )
                            }

                            ProgressBar(
                                progress = manualWriteHoldProgress,
                                fillColor = progressColor,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .width(200.dp)
                            )

                            Box(
                                modifier = Modifier.height(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isCurrentConfigWritten && writeErrorText != null) {
                                    Text(
                                        text = writeErrorText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
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