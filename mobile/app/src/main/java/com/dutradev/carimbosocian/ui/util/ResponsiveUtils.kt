package com.dutradev.carimbosocian.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize {
    Compact,   // Celulares em modo retrato (< 600dp)
    Medium,    // Tablets em modo retrato, celulares em modo paisagem (600dp - 839dp)
    Expanded   // Tablets grandes, tablets em modo paisagem (>= 840dp)
}

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    return when {
        screenWidth < 600.dp -> WindowSize.Compact
        screenWidth < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}

@Composable
fun isTabletOrLandscape(): Boolean {
    return rememberWindowSize() != WindowSize.Compact
}
