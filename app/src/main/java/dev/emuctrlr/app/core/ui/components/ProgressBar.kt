package dev.emuctrlr.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val coercedProgress = progress.coerceIn(0f, 1f)
    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .height(3.dp)
            .clip(shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(coercedProgress)
                .clip(shape)
                .background(fillColor)
        )
    }
}