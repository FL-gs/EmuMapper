package com.example.pairingapp.features.onboarding.emulators

import androidx.compose.foundation.border
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
import androidx.compose.runtime.saveable.rememberSaveable
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
    onBack: () -> Unit,
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
    val backFocusRequester = remember { FocusRequester() }
    val finishFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        installed = detector.installedEmulators()
        rootFocusRequester.requestFocus()
    }

    val finishIndex = installed.size
    val backIndex = installed.size + 1

    LaunchedEffect(installed) {
        focusedIndex = focusedIndex.coerceIn(0, backIndex)
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
                            focusedIndex == finishIndex -> onDone()
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
                    vertical = 64.dp
                )
                .border(2.dp, color = Color.Red)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_emulators_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.onboarding_emulators_explanation),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                EmulatorListContent(
                    installed = installed,
                    enabledEmulators = enabledEmulators,
                    focusedIndex = focusedIndex,
                    onToggle = ::toggleEmulator
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
            onClick = onDone,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .widthIn(min = 120.dp, max = 160.dp)
        )
    }
}

@Composable
private fun EmulatorListContent(
    installed: List<EmulatorDef>,
    enabledEmulators: Set<String>,
    focusedIndex: Int,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
        .border(2.dp, color = Color.Blue)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (installed.isEmpty()) {
            Text(
                text = stringResource(R.string.no_emulators_detected),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        } else {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                installed.forEachIndexed { index, emulator ->
                    OnboardingEmulatorRow(
                        emulator = emulator,
                        enabled = enabledEmulators.contains(emulator.id),
                        focused = focusedIndex == index,
                        onToggle = {
                            onToggle(emulator.id)
                        }
                    )
                }
            }
        }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
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