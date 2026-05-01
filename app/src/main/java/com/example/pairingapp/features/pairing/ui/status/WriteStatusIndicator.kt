package com.example.pairingapp.features.pairing.ui.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pairingapp.core.ui.components.ControllerHintStyle
import kotlinx.coroutines.delay

@Composable
fun WriteStatusIndicator(
    progress: Float,
    writing: Boolean,
    completed: Boolean,
    controllerHintStyle: ControllerHintStyle,
    modifier: Modifier = Modifier
) {
    var circleVisible by remember { mutableStateOf(false) }

    val shouldHideText = writing || progress > 0f || completed
    val displayedProgress = if (completed) 1f else progress.coerceIn(0f, 1f)
    val successColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(shouldHideText) {
        if (shouldHideText) {
            delay(WriteStatusDefaults.CircleDelayMillis)
            circleVisible = true
        } else {
            circleVisible = false
        }
    }

    Box(
        modifier = modifier.height(WriteStatusDefaults.StatusHeight),
        contentAlignment = Alignment.Center
    ) {
        HoldStartWritePrompt(
            hidden = shouldHideText,
            controllerHintStyle = controllerHintStyle
        )

        AnimatedWriteProgressCircle(
            progress = displayedProgress,
            visible = circleVisible,
            completed = completed,
            successColor = successColor
        )

        AnimatedSuccessCheck(
            visible = completed && circleVisible,
            color = successColor
        )
    }
}