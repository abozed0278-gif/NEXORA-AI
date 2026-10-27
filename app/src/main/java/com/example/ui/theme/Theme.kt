package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NexoraColorScheme = darkColorScheme(
    primary = NexoraCyan,
    onPrimary = NexoraBlack,
    primaryContainer = NexoraCyanDark,
    onPrimaryContainer = Color.White,
    secondary = NexoraViolet,
    onSecondary = Color.White,
    secondaryContainer = NexoraVioletDark,
    onSecondaryContainer = Color.White,
    tertiary = NexoraSilver,
    onTertiary = NexoraBlack,
    background = NexoraBlack,
    onBackground = NexoraSilverLight,
    surface = NexoraDarkSurface,
    onSurface = NexoraSilverLight,
    surfaceVariant = NexoraCardSurface,
    onSurfaceVariant = NexoraSilver,
    outline = NexoraBorder,
    outlineVariant = NexoraBorderGlow
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexoraColorScheme,
        typography = AppTypography,
        content = content
    )
}
