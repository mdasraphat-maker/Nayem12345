package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MayaPink,
    onPrimary = Color.White,
    primaryContainer = MayaPinkDark,
    onPrimaryContainer = MayaPinkLight,
    secondary = MayaViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF311B92),
    onSecondaryContainer = MayaVioletLight,
    tertiary = MayaCyan,
    onTertiary = Color.Black,
    background = MayaDarkBackground,
    onBackground = MayaTextPrimary,
    surface = MayaSurface,
    onSurface = MayaTextPrimary,
    surfaceVariant = MayaSurfaceVariant,
    onSurfaceVariant = MayaTextSecondary,
    outline = Color(0xFF363952),
    outlineVariant = Color(0xFF27293D)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    // Maya AI Assistant uses a distinctive rich dark-neon theme
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
