package com.example.pairingapp.features.settings.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.animation.rememberPulsingColor
import com.example.pairingapp.core.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    value: String,
    active: Boolean,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    selectorWidth: Int = 180,
    hasPreviousValue: Boolean,
    hasNextValue: Boolean,
    onPreviousValue: () -> Boolean,
    onNextValue: () -> Boolean,
    onFocused: (() -> Unit)? = null
) {
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun triggerShake() {
        scope.launch {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(2f, animationSpec = tween(35))
            shakeOffset.animateTo(-2f, animationSpec = tween(35))
            shakeOffset.animateTo(1f, animationSpec = tween(30))
            shakeOffset.animateTo(0f, animationSpec = tween(30))
        }
    }

    var isFocused by remember { mutableStateOf(false) }

    val pulsingBorderColor = rememberPulsingColor(
        enabled = active && isFocused,
        initialColor = MaterialTheme.colorScheme.primary,
        targetColor = MaterialTheme.colorScheme.secondary,
        durationMillis = 600
    )

    val borderColor = if (active && isFocused) {
        pulsingBorderColor
    } else {
        Color.Transparent
    }

    BoxWithConstraints {
        val isCompact = maxWidth < 500.dp

        if (isCompact) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent, RoundedCornerShape(6.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) onFocused?.invoke()
                    }
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        if (!active) return@onPreviewKeyEvent false
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                        when (mapKeyEvent(event.nativeKeyEvent)) {
                            PadKey.LEFT -> {
                                val changed = onPreviousValue()
                                if (!changed) triggerShake()
                                true
                            }

                            PadKey.RIGHT -> {
                                val changed = onNextValue()
                                if (!changed) triggerShake()
                                true
                            }

                            else -> false
                        }
                    }
                    .focusable(enabled = active)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }

                ConsoleOptionSelector(
                    value = value,
                    shakeOffsetX = shakeOffset.value,
                    hasPreviousValue = hasPreviousValue,
                    hasNextValue = hasNextValue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent, RoundedCornerShape(6.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) onFocused?.invoke()
                    }
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        if (!active) return@onPreviewKeyEvent false
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                        when (mapKeyEvent(event.nativeKeyEvent)) {
                            PadKey.LEFT -> {
                                val changed = onPreviousValue()
                                if (!changed) triggerShake()
                                true
                            }

                            PadKey.RIGHT -> {
                                val changed = onNextValue()
                                if (!changed) triggerShake()
                                true
                            }

                            else -> false
                        }
                    }
                    .focusable(enabled = active)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )
                }

                ConsoleOptionSelector(
                    value = value,
                    shakeOffsetX = shakeOffset.value,
                    hasPreviousValue = hasPreviousValue,
                    hasNextValue = hasNextValue,
                    modifier = Modifier.width(selectorWidth.dp)
                )
            }
        }
    }
}

@Composable
private fun ConsoleOptionSelector(
    value: String,
    modifier: Modifier = Modifier,
    shakeOffsetX: Float = 0f,
    hasPreviousValue: Boolean,
    hasNextValue: Boolean
) {
    val arrowColor = MaterialTheme.colorScheme.onSurface.copy(0.6f)
    val disabledArrowColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val valueColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectorArrow("<", color = if (hasPreviousValue) arrowColor else disabledArrowColor)

        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .offset(x = shakeOffsetX.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )

        SelectorArrow(">", color = if (hasNextValue) arrowColor else disabledArrowColor)
    }
}

@Composable
private fun SelectorArrow(
    symbol: String,
    color: Color
) {
    Box(
        modifier = Modifier.width(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

