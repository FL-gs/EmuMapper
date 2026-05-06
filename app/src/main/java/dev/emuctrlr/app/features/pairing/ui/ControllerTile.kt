package dev.emuctrlr.app.features.pairing.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import dev.emuctrlr.app.R
import dev.emuctrlr.app.core.input.ControllerDisplay
import dev.emuctrlr.app.core.input.ControllerInfo
import kotlinx.coroutines.launch

@Composable
fun ControllerTile(
    playerIndex: Int,
    uiKey: String,
    controller: ControllerInfo,
    size: Dp,
    minTile: Dp,
    maxTile: Dp,
    modifier: Modifier = Modifier
) {
    val scale = remember(uiKey) { Animatable(0.92f) }
    val alpha = remember(uiKey) { Animatable(0f) }

    LaunchedEffect(uiKey) {
        scale.snapTo(0.92f)
        alpha.snapTo(0f)

        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 220,
                    easing = FastOutSlowInEasing
                )
            )
        }

        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 180)
        )
    }

    val t = ((size.value - minTile.value) / (maxTile.value - minTile.value))
        .coerceIn(0f, 1f)

    val spacing = lerpDp(6.dp, 24.dp, t)
    val padding = lerpDp(6.dp, 16.dp, t)

    val titleMin = 8.sp
    val titleMax = MaterialTheme.typography.titleMedium.fontSize
    val titleSize = lerp(titleMin, titleMax, t)

    val subMin = 5.sp
    val subMax = MaterialTheme.typography.labelMedium.fontSize
    val subSize = lerp(subMin, subMax, t)

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.player_format, playerIndex),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = titleSize,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = ControllerDisplay.displayNameFor(controller),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = subSize,
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(spacing))

            Image(
                painter = painterResource(
                    id = ControllerDisplay.iconResFor(controller)
                ),
                contentDescription = controller.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

private fun lerpDp(start: Dp, end: Dp, t: Float): Dp =
    (start.value + (end.value - start.value) * t).dp