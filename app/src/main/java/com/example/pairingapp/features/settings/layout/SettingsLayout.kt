package com.example.pairingapp.features.settings.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pairingapp.core.ui.components.ActionHint
import com.example.pairingapp.core.ui.components.ActionHintBar

@Composable
fun SettingsLayout(
    sidebar: @Composable () -> Unit,
    content: @Composable () -> Unit,
    footerHints: List<ActionHint>,
    modifier: Modifier = Modifier,
    sidebarWidth: Int = 180
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar (menu gauche)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(sidebarWidth.dp)
            ) {
                sidebar()
            }

            // Séparateur
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            // Contenu
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {
                content()
            }
        }

        ActionHintBar(
            hints = footerHints,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
