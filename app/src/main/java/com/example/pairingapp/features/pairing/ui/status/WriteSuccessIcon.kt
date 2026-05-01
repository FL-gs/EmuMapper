package com.example.pairingapp.features.pairing.ui.status

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun WriteSuccessIcon(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = WriteStatusDefaults.SuccessColor
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedWriteProgressCircle(
            progress = 1f,
            visible = visible,
            completed = visible,
            successColor = color
        )

        AnimatedSuccessCheck(
            visible = visible,
            color = color
        )
    }
}