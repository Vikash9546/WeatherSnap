package com.weathersnap.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BackgroundColor = Color(0xFF0D1006)
val SurfaceColor = Color(0xFF1C2010)
val AccentColor = Color(0xFFB5C75A)
val OnAccentColor = Color(0xFF0D1006)
val CardBorderColor = Color(0xFF2E3820)
val OnBackgroundColor = Color(0xFFE8F0D0)
val OnSurfaceColor = Color(0xFFD0DBA8)
val SecondaryTextColor = Color(0xFF8A9A60)
val ErrorColor = Color(0xFFE57373)
val SuccessColor = Color(0xFF81C784)

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor,
    onPrimary = OnAccentColor,
    primaryContainer = Color(0xFF2A3614),
    onPrimaryContainer = AccentColor,
    secondary = Color(0xFF8A9A60),
    onSecondary = BackgroundColor,
    secondaryContainer = Color(0xFF1C2810),
    onSecondaryContainer = OnSurfaceColor,
    tertiary = Color(0xFF6B8E6B),
    onTertiary = BackgroundColor,
    background = BackgroundColor,
    onBackground = OnBackgroundColor,
    surface = SurfaceColor,
    onSurface = OnSurfaceColor,
    surfaceVariant = Color(0xFF1E2614),
    onSurfaceVariant = SecondaryTextColor,
    outline = CardBorderColor,
    outlineVariant = Color(0xFF252E14),
    error = ErrorColor,
    onError = BackgroundColor,
    errorContainer = Color(0xFF3D1010),
    onErrorContainer = ErrorColor,
)

@Composable
fun WeatherSnapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = WeatherSnapTypography,
        content = content
    )
}
