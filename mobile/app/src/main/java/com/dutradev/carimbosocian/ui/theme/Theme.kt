package com.dutradev.carimbosocian.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SkyPrimaryDark,
    onPrimary = SkyOnPrimaryDark,
    primaryContainer = SkyPrimaryContainerDark,
    onPrimaryContainer = SkyOnPrimaryContainerDark,
    background = SkyBackgroundDark,
    surface = SkySurfaceDark,
    onBackground = SkyOnSurfaceDark,
    onSurface = SkyOnSurfaceDark,
    surfaceVariant = SkySurfaceVariantDark,
    outline = SkyOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SkyPrimaryLight,
    onPrimary = SkyOnPrimaryLight,
    primaryContainer = SkyPrimaryContainerLight,
    onPrimaryContainer = SkyOnPrimaryContainerLight,
    background = SkyBackgroundLight,
    surface = SkySurfaceLight,
    onBackground = SkyOnSurfaceLight,
    onSurface = SkyOnSurfaceLight,
    surfaceVariant = SkySurfaceVariantLight,
    outline = SkyOutlineLight
)

@Composable
fun CarimboxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
