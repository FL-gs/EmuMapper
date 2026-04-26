package com.example.pairingapp.features.settings.emulators

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.pairingapp.R
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.components.CustomSwitch
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.data.emulators.EmulatorDef
import com.example.pairingapp.data.emulators.EmulatorDetector

@Composable
fun EmulatorsScreen(
    active: Boolean,
    enabledPackages: Set<String>,
    onSetEnabledPackages: (Set<String>) -> Unit,
    onRequestEnableRetroArch: ((String) -> Unit)? = null,
    onHintStateChanged: (HintBarState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val detector = remember { EmulatorDetector(context) }

    var installed by remember { mutableStateOf<List<EmulatorDef>>(emptyList()) }
    var focusIndex by remember { mutableIntStateOf(0) }

    val focusRequester = remember { FocusRequester() }
    val previewAlpha = if (active) 1f else 0.35f

    fun refreshInstalledEmulators() {
        installed = detector.installedEmulators()
        focusIndex = focusIndex.coerceIn(0, (installed.size - 1).coerceAtLeast(0))
    }

    LaunchedEffect(Unit) {
        refreshInstalledEmulators()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshInstalledEmulators()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(installed, enabledPackages) {
        val installedIds = installed.map { it.id }.toSet()
        val cleaned = enabledPackages.intersect(installedIds)

        if (cleaned != enabledPackages) {
            onSetEnabledPackages(cleaned)
        }
    }

    LaunchedEffect(active) {
        if (active) focusRequester.requestFocus()
    }

    LaunchedEffect(active, focusIndex, installed, enabledPackages) {
        if (!active) return@LaunchedEffect

        val focusedEmulator = installed.getOrNull(focusIndex)
        val isEnabled = focusedEmulator?.id?.let(enabledPackages::contains) == true

        onHintStateChanged(
            HintBarState.Emulators(focusedEnabled = isEnabled)
        )
    }

    val inputModifier = Modifier
        .focusRequester(focusRequester)
        .focusable(enabled = active)
        .onPreviewKeyEvent { event ->
            if (!active) return@onPreviewKeyEvent false
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

            when (mapKeyEvent(event.nativeKeyEvent)) {
                PadKey.UP -> {
                    focusIndex = (focusIndex - 1).coerceAtLeast(0)
                    true
                }

                PadKey.DOWN -> {
                    focusIndex = (focusIndex + 1)
                        .coerceAtMost((installed.size - 1).coerceAtLeast(0))
                    true
                }

                PadKey.A -> {
                    if (installed.isNotEmpty()) {
                        val emulator = installed[focusIndex]
                        val emulatorId = emulator.id
                        val isEnabled = enabledPackages.contains(emulatorId)

                        if (!isEnabled && emulatorId == "retroarch") {
                            onRequestEnableRetroArch?.invoke(emulatorId)
                        } else {
                            val next = if (isEnabled) {
                                enabledPackages - emulatorId
                            } else {
                                enabledPackages + emulatorId
                            }

                            onSetEnabledPackages(next)
                        }
                    }
                    true
                }

                else -> false
            }
        }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(inputModifier)
            .graphicsLayer { alpha = previewAlpha },
        contentAlignment = Alignment.Center
    ) {
        if (installed.isEmpty()) {
            Text(
                text = stringResource(R.string.no_emulators_detected),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                installed.forEachIndexed { index, emulator ->
                    EmulatorRow(
                        emulator = emulator,
                        enabled = enabledPackages.contains(emulator.id),
                        focused = active && index == focusIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun EmulatorRow(
    emulator: EmulatorDef,
    enabled: Boolean,
    focused: Boolean
) {
    val textColor = if (focused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val checkedTrackColor = MaterialTheme.colorScheme.primary
    val uncheckedTrackColor = MaterialTheme.colorScheme.outline
    val thumbColor = Color.White

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        CustomSwitch(
            checked = enabled,
            onCheckedChange = {},
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            thumbColor = thumbColor,
            enabled = false,
            focused = focused
        )

        Text(
            text = emulator.label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}