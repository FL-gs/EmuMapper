package com.example.pairingapp.core.ui.animation

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun rememberPulsingColor(
    enabled: Boolean,
    initialColor: Color,
    targetColor: Color,
    durationMillis: Int = 1000
): Color {
    if (!enabled) return initialColor

    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_color_transition")

    return infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsing_color"
    ).value
}