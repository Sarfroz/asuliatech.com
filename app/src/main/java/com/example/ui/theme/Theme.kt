package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AsuliaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF581C87),
    secondary = AsuliaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAE8FF),
    onSecondaryContainer = Color(0xFF701A75),
    tertiary = AsuliaTertiary,
    background = AsuliaBackground,
    onBackground = AsuliaTextPrimary,
    surface = AsuliaSurface,
    onSurface = AsuliaTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = AsuliaTextSecondary,
    outline = AsuliaCardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = AsuliaSecondary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF581C87),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = AsuliaTertiary,
    onSecondary = Color.Black,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun AsuliaTechTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AsuliaTechTheme(darkTheme = darkTheme, content = content)
}
