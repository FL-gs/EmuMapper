package dev.emumapper.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.emumapper.app.core.ui.theme.Neutral700

@Composable
fun SectionDivider() {
    Spacer(modifier = Modifier.height(12.dp))

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Neutral700.copy(alpha = 0.18f))
    )

    Spacer(modifier = Modifier.height(12.dp))
}