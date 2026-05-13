package dev.emumapper.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light theme
private val LightColorScheme = lightColorScheme(
    background = Neutral500,
    surface = Neutral550,
    surfaceVariant = Neutral300,
    primary = Primary500,
    secondary = Secondary500,
    tertiary = tertiary500,
    onSurface = Neutral900,
    onSurfaceVariant = Neutral800,
    outline = Neutral700,
    error = Error500,
)

// Dark theme
private val DarkColorScheme = darkColorScheme(
    background = Neutral950,
    surface = Neutral960,
    surfaceVariant = Neutral900,
    primary = Primary500,
    secondary = Secondary500,
    onBackground = Neutral900,
    onSurface = Neutral500,
    onSurfaceVariant = Neutral600,
    outline = Neutral900,
    error = Error500,
)

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}