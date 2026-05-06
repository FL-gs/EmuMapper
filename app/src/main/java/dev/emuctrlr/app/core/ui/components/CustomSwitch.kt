package dev.emuctrlr.app.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedTrackColor: Color,
    uncheckedTrackColor: Color,
    thumbColor: Color = Color.White,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    focused: Boolean = false
) {
    val trackWidth = 30.dp
    val trackHeight = 16.dp
    val padding = 2.dp
    val thumbSize = trackHeight - padding * 2

    val maxOffset = trackWidth - thumbSize - padding * 2

    val offsetX by animateDpAsState(
        targetValue = if (checked) maxOffset else 0.dp,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "switch_offset"
    )

    val trackColor = if (checked) checkedTrackColor else uncheckedTrackColor

    val borderColor = rememberPulsingColor(
        enabled = focused,
        initialColor = MaterialTheme.colorScheme.secondary,
        targetColor = MaterialTheme.colorScheme.background,
        durationMillis = 600
    )

    val borderModifier = if (focused) {
        Modifier.border(
            width = 2.dp,
            color = borderColor,
            shape = RoundedCornerShape(50)
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .width(trackWidth + 4.dp)
            .height(trackHeight + 4.dp)
            .then(borderModifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(trackHeight)
                .clip(RoundedCornerShape(50))
                .background(trackColor)
                .clickable(
                    enabled = enabled,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onCheckedChange(!checked)
                }
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
    }
}