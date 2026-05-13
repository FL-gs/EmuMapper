package dev.emumapper.app.features.settings.layout

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSidebar(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    isHub: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val indicatorHeight = 25.dp
    val indicatorWidth = 3.dp

    var sidebarTopInRoot by remember { mutableFloatStateOf(0f) }

    val itemCenters = remember(items.size) {
        mutableStateListOf<Dp?>().apply {
            repeat(items.size) { add(null) }
        }
    }

    val measuredCenter = itemCenters.getOrNull(selectedIndex)

    val indicatorOffsetY by animateDpAsState(
        targetValue = measuredCenter?.minus(indicatorHeight / 2) ?: 0.dp,
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "sidebar_indicator_offset"
    )

    Surface(
        modifier = modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    sidebarTopInRoot = coordinates.boundsInRoot().top
                }
        ) {
            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items.forEachIndexed { index, label ->
                        SidebarItem(
                            title = label,
                            active = index == selectedIndex,
                            isHub = isHub,
                            sidebarTopInRoot = sidebarTopInRoot,
                            onCenterMeasured = { centerY ->
                                itemCenters[index] = centerY
                            }
                        )
                    }
                }
            }

            if (measuredCenter != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = indicatorOffsetY)
                        .width(indicatorWidth)
                        .height(indicatorHeight)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
private fun SidebarItem(
    title: String,
    active: Boolean,
    isHub: Boolean,
    sidebarTopInRoot: Float,
    onCenterMeasured: (Dp) -> Unit
) {
    val density = LocalDensity.current

    val textColor by animateColorAsState(
        targetValue = if (active) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 300),
        label = "sidebar_text_color"
    )

    val textAlpha = if (isHub || active) 1f else 0.35f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                val centerYInSidebar = bounds.top + (bounds.height / 2f) - sidebarTopInRoot
                onCenterMeasured(with(density) { centerYInSidebar.toDp() })
            }
            .padding(horizontal = 32.dp, vertical = 8.dp),

        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor.copy(alpha = textAlpha)
        )
    }
}