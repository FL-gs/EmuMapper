package com.example.pairingapp.features.settings.emulators

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.data.emulators.EmulatorDef
import com.example.pairingapp.data.emulators.EmulatorDetector
import com.example.pairingapp.features.emulators.components.EmulatorToggleList

@Composable
fun EmulatorsScreen(
    active: Boolean,
    enabledPackages: Set<String>,
    onSetEnabledPackages: (Set<String>) -> Unit,
    onHintStateChanged: (HintBarState) -> Unit,
    modifier: Modifier = Modifier,
    onRequestEnableRetroArch: ((String) -> Unit)? = null,
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
        EmulatorToggleList(
            installed = installed,
            enabledEmulators = enabledPackages,
            focusedIndex = if (active) focusIndex else -1,
        )
    }
}


