package com.example.pairingapp.features.onboarding.emulators

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pairingapp.R
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.components.ActionButton
import com.example.pairingapp.core.ui.components.CustomSwitch
import com.example.pairingapp.data.emulators.EmulatorDef
import com.example.pairingapp.data.emulators.EmulatorDetector

@Composable
fun OnboardingEmulatorsSetupScreen(
    enabledEmulators: Set<String>,
    onSetEnabledEmulators: (Set<String>) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val detector = remember { EmulatorDetector(context) }

    var installed by remember {
        mutableStateOf<List<EmulatorDef>>(emptyList())
    }

    var focusedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val rootFocusRequester = remember { FocusRequester() }
    val finishFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        installed = detector.installedEmulators()
        rootFocusRequester.requestFocus()
    }

    val finishIndex = installed.size

    LaunchedEffect(installed) {
        focusedIndex = focusedIndex.coerceIn(
            0,
            installed.size.coerceAtLeast(0)
        )
    }

    fun toggleEmulator(emulatorId: String) {
        val next = if (enabledEmulators.contains(emulatorId)) {
            enabledEmulators - emulatorId
        } else {
            enabledEmulators + emulatorId
        }

        onSetEnabledEmulators(next)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(rootFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) {
                    return@onPreviewKeyEvent false
                }

                when (mapKeyEvent(event.nativeKeyEvent)) {
                    PadKey.UP -> {
                        focusedIndex = (focusedIndex - 1).coerceAtLeast(0)
                        true
                    }

                    PadKey.DOWN -> {
                        focusedIndex = (focusedIndex + 1).coerceAtMost(finishIndex)
                        true
                    }

                    PadKey.A -> {
                        val emulator = installed.getOrNull(focusedIndex)

                        if (emulator != null) {
                            toggleEmulator(emulator.id)
                        } else {
                            onDone()
                        }

                        true
                    }

                    else -> false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_emulators_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.onboarding_emulators_explanation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            if (installed.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_emulators_detected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.widthIn(max = 320.dp)
                ) {
                    installed.forEachIndexed { index, emulator ->
                        OnboardingEmulatorRow(
                            emulator = emulator,
                            enabled = enabledEmulators.contains(emulator.id),
                            focused = focusedIndex == index,
                            onToggle = {
                                toggleEmulator(emulator.id)
                            }
                        )
                    }
                }
            }
        }

        ActionButton(
            text = stringResource(R.string.finish),
            selected = focusedIndex == finishIndex,
            active = true,
            focusRequester = finishFocusRequester,
            focused = focusedIndex == finishIndex,
            onClick = onDone,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .widthIn(min = 120.dp, max = 160.dp)
        )
    }
}

@Composable
private fun OnboardingEmulatorRow(
    emulator: EmulatorDef,
    enabled: Boolean,
    focused: Boolean,
    onToggle: () -> Unit
) {
    val textColor = if (focused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CustomSwitch(
            checked = enabled,
            onCheckedChange = { onToggle() },
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline,
            thumbColor = Color.White,
            enabled = true,
            focused = focused
        )

        Text(
            text = emulator.label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}