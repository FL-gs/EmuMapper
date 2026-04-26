package com.example.pairingapp.features.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pairingapp.R
import com.example.pairingapp.core.input.DeviceChoice
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import kotlinx.coroutines.android.awaitFrame

@Composable
fun SelectionDialog(
    title: String,
    choices: List<DeviceChoice>,
    current: String?,
    onPick: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIndex by remember(current, choices) {
        mutableIntStateOf(
            choices.indexOfFirst { it.key == current }
                .let { if (it >= 0) it else 0 }
        )
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(choices, current) {
        selectedIndex = choices.indexOfFirst { it.key == current }
            .let { if (it >= 0) it else 0 }

        awaitFrame()
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                        when (mapKeyEvent(event.nativeKeyEvent)) {
                            PadKey.UP -> {
                                selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                                true
                            }

                            PadKey.DOWN -> {
                                selectedIndex =
                                    (selectedIndex + 1).coerceAtMost((choices.size - 1).coerceAtLeast(0))
                                true
                            }

                            PadKey.A -> {
                                val picked = choices.getOrNull(selectedIndex)?.key
                                onPick(picked)
                                true
                            }

                            PadKey.B -> {
                                onDismiss()
                                true
                            }

                            else -> false
                        }
                    }
            ) {
                if (choices.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_controllers_detected),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 320.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        itemsIndexed(choices) { index, item ->
                            val isSelected = index == selectedIndex

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPick(item.key) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null
                                )

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}