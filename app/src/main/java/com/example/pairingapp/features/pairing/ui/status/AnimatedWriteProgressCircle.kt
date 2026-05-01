package com.example.pairingapp.features.pairing.ui.status

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimatedWriteProgressCircle(
    progress: Float,
    visible: Boolean,
    completed: Boolean,
    modifier: Modifier = Modifier,
    successColor: Color = WriteStatusDefaults.SuccessColor
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = WriteStatusDefaults.CircleAlphaDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "writeProgressCircleAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = if (visible) {
            keyframes {
                durationMillis = WriteStatusDefaults.CircleScaleInDurationMillis
                1.12f at 120 using FastOutSlowInEasing
                1.00f at WriteStatusDefaults.CircleScaleInDurationMillis using FastOutSlowInEasing
            }
        } else {
            tween(
                durationMillis = WriteStatusDefaults.CircleScaleOutDurationMillis,
                easing = FastOutSlowInEasing
            )
        },
        label = "writeProgressCircleScale"
    )

    val successAlpha by animateFloatAsState(
        targetValue = if (completed) 1f else 0f,
        animationSpec = tween(
            durationMillis = WriteStatusDefaults.CircleColorDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "writeProgressCircleSuccessAlpha"
    )

    val gradientRotation = remember { Animatable(0f) }

    val progressGradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primary
    )

    LaunchedEffect(visible, completed) {
        if (visible && !completed) {
            while (true) {
                gradientRotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(
                        durationMillis = WriteStatusDefaults.ProgressGradientRotationDurationMillis,
                        easing = LinearEasing
                    )
                )
                gradientRotation.snapTo(0f)
            }
        } else {
            gradientRotation.snapTo(0f)
        }
    }

    WriteProgressCircle(
        progress = progress.coerceIn(0f, 1f),
        successAlpha = successAlpha,
        gradientRotation = gradientRotation.value,
        progressGradientColors = progressGradientColors,
        successColor = successColor,
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
        }
    )
}

@Composable
private fun WriteProgressCircle(
    progress: Float,
    successAlpha: Float,
    gradientRotation: Float,
    progressGradientColors: List<Color>,
    successColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(WriteStatusDefaults.CircleSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = WriteStatusDefaults.CircleStrokeWidth.toPx()
            val diameter = size.minDimension - strokeWidth

            val topLeft = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )

            val arcSize = Size(diameter, diameter)

            val progressBrush = Brush.sweepGradient(
                colors = progressGradientColors,
                center = center
            )

            rotate(
                degrees = gradientRotation,
                pivot = center
            ) {
                drawCircle(
                    brush = progressBrush,
                    radius = diameter / 2f,
                    center = center,
                    alpha = WriteStatusDefaults.ProgressTrackAlpha * (1f - successAlpha),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )

                drawArc(
                    brush = progressBrush,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    alpha = 1f - successAlpha,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }

            drawCircle(
                color = successColor,
                radius = diameter / 2f,
                center = center,
                alpha = WriteStatusDefaults.ProgressTrackAlpha * successAlpha,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = successColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                alpha = successAlpha,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}