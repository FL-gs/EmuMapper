package com.example.pairingapp.features.pairing.ui.status

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun AnimatedSuccessCheck(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = WriteStatusDefaults.SuccessColor
) {
    val drawProgress = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.85f) }

    LaunchedEffect(visible) {
        if (visible) {
            drawProgress.snapTo(0f)
            alpha.snapTo(0f)
            scale.snapTo(0.85f)

            coroutineScope {
                launch {
                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = WriteStatusDefaults.CheckAlphaDurationMillis,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    scale.animateTo(
                        targetValue = 1.10f,
                        animationSpec = tween(
                            durationMillis = WriteStatusDefaults.CheckScaleUpDurationMillis,
                            easing = FastOutSlowInEasing
                        )
                    )

                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = WriteStatusDefaults.CheckScaleDownDurationMillis,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    drawProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = WriteStatusDefaults.CheckDrawDurationMillis,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        } else {
            drawProgress.snapTo(0f)
            alpha.snapTo(0f)
            scale.snapTo(0.85f)
        }
    }

    Canvas(
        modifier = modifier
            .size(WriteStatusDefaults.CheckSize)
            .graphicsLayer {
                this.alpha = alpha.value
                scaleX = scale.value
                scaleY = scale.value
            }
    ) {
        drawAnimatedCheckMark(
            progress = drawProgress.value,
            color = color
        )
    }
}

private fun DrawScope.drawAnimatedCheckMark(
    progress: Float,
    color: Color
) {
    val strokeWidth = WriteStatusDefaults.CheckStrokeWidth.toPx()

    val start = Offset(
        x = size.width * 0.18f,
        y = size.height * 0.52f
    )

    val middle = Offset(
        x = size.width * 0.40f,
        y = size.height * 0.74f
    )

    val end = Offset(
        x = size.width * 0.82f,
        y = size.height * 0.28f
    )

    val firstSegmentEndProgress = 0.42f

    if (progress <= firstSegmentEndProgress) {
        val segmentProgress = progress / firstSegmentEndProgress

        drawLine(
            color = color,
            start = start,
            end = lerpOffset(start, middle, segmentProgress),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    } else {
        drawLine(
            color = color,
            start = start,
            end = middle,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        val segmentProgress =
            (progress - firstSegmentEndProgress) / (1f - firstSegmentEndProgress)

        drawLine(
            color = color,
            start = middle,
            end = lerpOffset(middle, end, segmentProgress),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun lerpOffset(
    start: Offset,
    end: Offset,
    progress: Float
): Offset {
    return Offset(
        x = start.x + (end.x - start.x) * progress,
        y = start.y + (end.y - start.y) * progress
    )
}