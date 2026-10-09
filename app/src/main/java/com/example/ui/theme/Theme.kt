package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val HighDensityLightColorScheme = lightColorScheme(
    primary = HighDensityAccent,
    onPrimary = Color.White,
    secondary = HighDensityTabActive,
    onSecondary = HighDensityTabActiveText,
    tertiary = HighDensityTabActiveText,
    background = HighDensityBackground,
    onBackground = HighDensityText,
    surface = Color.White,
    onSurface = HighDensityText,
    surfaceVariant = HighDensitySurface,
    onSurfaceVariant = HighDensityText,
    primaryContainer = HighDensitySurface,
    onPrimaryContainer = HighDensityText,
    secondaryContainer = HighDensityTabActive,
    onSecondaryContainer = HighDensityTabActiveText,
    outline = Color(0xFFD1D5DB), // gray-300 style
    outlineVariant = Color(0xFFE5E7EB) // gray-200 style
)

private val HighDensityDarkColorScheme = darkColorScheme(
    primary = HighDensityAccent,
    onPrimary = Color.White,
    secondary = HighDensityTabActive,
    onSecondary = HighDensityTabActiveText,
    tertiary = HighDensityTabActiveText,
    background = Color(0xFF121214),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1E1E22),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF2A2A2F),
    onSurfaceVariant = Color(0xFFE3E2E6),
    primaryContainer = Color(0xFF2A2A2F),
    onPrimaryContainer = Color(0xFFE3E2E6),
    secondaryContainer = Color(0xFF2C324D),
    onSecondaryContainer = Color(0xFFDDE1FF),
    outline = Color(0xFF4B5563),
    outlineVariant = Color(0xFF374151)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic Android colors by default to preserve custom High Density theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        HighDensityDarkColorScheme
    } else {
        HighDensityLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
