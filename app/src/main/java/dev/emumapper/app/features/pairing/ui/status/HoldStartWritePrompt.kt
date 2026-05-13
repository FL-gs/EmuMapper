package dev.emumapper.app.features.pairing.ui.status

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.emumapper.app.R
import dev.emumapper.app.core.input.GamepadAction
import dev.emumapper.app.core.ui.components.rememberPulsingColor
import dev.emumapper.app.core.ui.components.ActionIconResolver
import dev.emumapper.app.core.ui.components.ControllerHintStyle

@Composable
fun HoldStartWritePrompt(
    hidden: Boolean,
    controllerHintStyle: ControllerHintStyle,
    modifier: Modifier = Modifier
) {
    val hold = stringResource(R.string.hold)
    val toWrite = stringResource(R.string.to_write)

    val promptColor = rememberPulsingColor(
        enabled = !hidden,
        initialColor = MaterialTheme.colorScheme.secondary,
        targetColor = MaterialTheme.colorScheme.primary,
        durationMillis = 600
    )

    val alpha by animateFloatAsState(
        targetValue = if (hidden) 0f else 1f,
        animationSpec = tween(
            durationMillis = WriteStatusDefaults.TextAlphaDurationMillis,
            easing = FastOutSlowInEasing
        ),
        label = "writePromptTextAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (hidden) 0.92f else 1f,
        animationSpec = if (hidden) {
            keyframes {
                durationMillis = WriteStatusDefaults.TextScaleOutDurationMillis
                1.06f at 70 using FastOutSlowInEasing
                0.92f at WriteStatusDefaults.TextScaleOutDurationMillis using FastOutSlowInEasing
            }
        } else {
            tween(
                durationMillis = WriteStatusDefaults.TextScaleInDurationMillis,
                easing = FastOutSlowInEasing
            )
        },
        label = "writePromptTextScale"
    )

    Row(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = hold,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = promptColor
        )

        Icon(
            painter = painterResource(
                id = ActionIconResolver.iconRes(
                    GamepadAction.START,
                    controllerHintStyle
                )
            ),
            contentDescription = "Start",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = toWrite,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = promptColor
        )
    }
}