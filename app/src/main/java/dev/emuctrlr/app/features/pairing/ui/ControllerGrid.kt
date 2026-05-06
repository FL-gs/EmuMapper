package dev.emuctrlr.app.features.pairing.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.emuctrlr.app.R
import dev.emuctrlr.app.features.pairing.VisibleControllerUi
import kotlin.math.ceil

@Composable
fun ControllerGrid(
    controllers: List<VisibleControllerUi>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val n = controllers.size

        if (n == 0) {
            Text(
                text = stringResource(R.string.connect_controllers_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
            return@BoxWithConstraints
        }

        val spacing = 14.dp
        val minTile = 120.dp
        val maxTile = 320.dp

        fun maxColsAtMin(): Int {
            for (cols in n downTo 1) {
                val needed = (minTile * cols) + (spacing * (cols - 1))
                if (needed <= maxWidth) return cols
            }
            return 1
        }

        val colsMin = maxColsAtMin()
        val rowsAtMin = ceil(n / colsMin.toFloat()).toInt()
        val useWrap = rowsAtMin >= 2

        val rows = if (useWrap) 2 else 1
        val cols = if (!useWrap) {
            n
        } else {
            ceil(n / 2f).toInt().coerceAtLeast(1)
        }

        val byWidth = (maxWidth - spacing * (cols - 1)) / cols
        val byHeight = (maxHeight - spacing * (rows - 1)) / rows
        val tileSize = minOf(byWidth, byHeight).coerceIn(minTile, maxTile)

        if (!useWrap) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                controllers.forEachIndexed { index, c ->
                    ControllerTile(
                        playerIndex = index + 1,
                        uiKey = c.uiKey,
                        controller = c.controller,
                        size = tileSize,
                        minTile = minTile,
                        maxTile = maxTile
                    )
                }
            }
        } else {
            TwoRowWrap(
                itemCount = n,
                cols = cols,
                itemSize = tileSize,
                spacing = spacing
            ) {
                controllers.forEachIndexed { index, c ->
                    ControllerTile(
                        playerIndex = index + 1,
                        uiKey = c.uiKey,
                        controller = c.controller,
                        size = tileSize,
                        minTile = minTile,
                        maxTile = maxTile
                    )
                }
            }
        }
    }
}

@Composable
private fun TwoRowWrap(
    itemCount: Int,
    cols: Int,
    itemSize: Dp,
    spacing: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val sizePx = with(density) { itemSize.roundToPx() }
    val spacingPx = with(density) { spacing.roundToPx() }
    val rows = ceil(itemCount / cols.toFloat()).toInt().coerceIn(1, 2)

    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val childConstraints = Constraints.fixed(sizePx, sizePx)
        val placeables = measurables.map { it.measure(childConstraints) }

        val layoutWidth = constraints.maxWidth
        val layoutHeight = rows * sizePx + (rows - 1) * spacingPx

        layout(layoutWidth, layoutHeight) {
            for (r in 0 until rows) {
                val start = r * cols
                val end = minOf(start + cols, placeables.size)
                val countInRow = end - start
                if (countInRow <= 0) continue

                val rowWidth = countInRow * sizePx + (countInRow - 1) * spacingPx
                val xStart = ((layoutWidth - rowWidth) / 2).coerceAtLeast(0)
                val y = r * (sizePx + spacingPx)

                var x = xStart
                for (i in start until end) {
                    placeables[i].placeRelative(x, y)
                    x += sizePx + spacingPx
                }
            }
        }
    }
}