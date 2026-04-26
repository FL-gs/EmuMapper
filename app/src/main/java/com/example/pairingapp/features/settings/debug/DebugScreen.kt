package com.example.pairingapp.features.settings.debug

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pairingapp.R
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.components.ActionButton
import com.example.pairingapp.core.ui.components.CustomSwitch
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.core.utils.AppLogger

@Composable
fun DebugScreen(
    active: Boolean,
    debugLogs: Boolean,
    onSetDebugLogs: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
    onHintStateChanged: (HintBarState) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val cleanLogsFocusRequester = remember { FocusRequester() }
    val previewAlpha = if (active) 1f else 0.35f

    val FOCUS_SWITCH = 0
    val FOCUS_BUTTON = 1

    var focusedIndex by remember { mutableStateOf(FOCUS_SWITCH) }
    var showClearedMessage by remember { mutableStateOf(false) }

    val logState by AppLogger.logFileState.collectAsState()

    LaunchedEffect(active) {
        if (active) focusRequester.requestFocus()
    }

    LaunchedEffect(active, debugLogs) {
        if (!active) return@LaunchedEffect

        onHintStateChanged(
            HintBarState.Debug(enabled = debugLogs)
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
                    focusedIndex = (focusedIndex - 1).coerceAtLeast(FOCUS_SWITCH)
                    true
                }

                PadKey.DOWN -> {
                    focusedIndex = (focusedIndex + 1).coerceAtMost(FOCUS_BUTTON)
                    true
                }

                PadKey.A -> {
                    when (focusedIndex) {
                        FOCUS_SWITCH -> onSetDebugLogs(!debugLogs)
                        FOCUS_BUTTON -> {
                            onClearLogs()
                            showClearedMessage = true
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
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.write_logs_to_file),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (active && focusedIndex == FOCUS_SWITCH) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                CustomSwitch(
                    checked = debugLogs,
                    onCheckedChange = onSetDebugLogs,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                    enabled = active,
                    focused = active && focusedIndex == FOCUS_SWITCH
                )
            }

            Text(
                text = stringResource(R.string.write_logs_to_file_desc),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionButton(
                text = stringResource(R.string.clean_logs),
                selected = focusedIndex == FOCUS_BUTTON,
                active = active,
                focusRequester = cleanLogsFocusRequester,
                focused = focusedIndex == FOCUS_BUTTON,
                onClick = onClearLogs
            )


            Box(
                modifier = Modifier.height(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showClearedMessage) {
                    Text(
                        text = stringResource(R.string.logs_cleared),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = logState) {
                    is AppLogger.LogFileState.Ready -> {
                        Text(
                            text = state.path,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    is AppLogger.LogFileState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {
                        // rien → espace vide mais réservé 👍
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 64.dp)
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Want to report a bug? suggest a QoL ? Contact me on Discord -> @Papayou",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}