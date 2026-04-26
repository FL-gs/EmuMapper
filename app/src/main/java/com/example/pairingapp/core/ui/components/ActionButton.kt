package com.example.pairingapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.example.pairingapp.core.ui.animation.rememberPulsingColor

@Composable
fun ActionButton(
    text: String,
    selected: Boolean,
    active: Boolean,
    focusRequester: FocusRequester,
    previousFocusRequester: FocusRequester? = null,
    nextFocusRequester: FocusRequester? = null,
    onClick: () -> Unit,
    onFocused: (() -> Unit)? = null,
    focused: Boolean = false,
    modifier: Modifier = Modifier

) {
    var isFocused by remember { mutableStateOf(false) }

    val showFocusedStyle = active && (isFocused || focused)

    val pulsingColor = rememberPulsingColor(
        enabled = showFocusedStyle,
        initialColor = MaterialTheme.colorScheme.primary,
        targetColor = MaterialTheme.colorScheme.secondary,
        durationMillis = 600
    )

    val borderColor = if (showFocusedStyle) {
        pulsingColor
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val borderWidth = if (showFocusedStyle) 2.dp else 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp)
            )
            .border(borderWidth, borderColor, RoundedCornerShape(4.dp))
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocused?.invoke()
            }
            .focusRequester(focusRequester)
            .focusProperties {
                previousFocusRequester?.let { up = it }
                nextFocusRequester?.let { down = it }
            }
            .onPreviewKeyEvent { event ->
                if (!active) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.key) {
                    Key.Enter,
                    Key.DirectionCenter,
                    Key.NumPadEnter,
                    Key.ButtonA -> {
                        onClick()
                        true
                    }

                    else -> false
                }
            }
            .focusable(enabled = active)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}