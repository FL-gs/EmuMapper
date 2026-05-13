package dev.emumapper.app.features.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.emumapper.app.R
import dev.emumapper.app.core.ui.components.CustomSwitch
import dev.emumapper.app.data.emulators.EmulatorDef

@Composable
fun EmulatorToggleList(
    installed: List<EmulatorDef>,
    enabledEmulators: Set<String>,
    focusedIndex: Int,
    modifier: Modifier = Modifier,
    rowSpacing: Dp = 8.dp,
    switchTextSpacing: Dp = 5.dp
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
                verticalArrangement = Arrangement.spacedBy(rowSpacing)
            ) {
                installed.forEachIndexed { index, emulator ->
                    EmulatorToggleRow(
                        emulator = emulator,
                        enabled = enabledEmulators.contains(emulator.id),
                        focused = focusedIndex == index,
                        switchTextSpacing = switchTextSpacing
                    )
                }
            }
        }
    }
}

@Composable
private fun EmulatorToggleRow(
    emulator: EmulatorDef,
    enabled: Boolean,
    focused: Boolean,
    switchTextSpacing: Dp
) {
    val textColor = if (focused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(switchTextSpacing)
    ) {
        CustomSwitch(
            checked = enabled,
            onCheckedChange = {},
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline,
            thumbColor = Color.White,
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


