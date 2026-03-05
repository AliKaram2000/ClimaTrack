package com.aeinae.climatrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ========================
// Light Color Scheme
// ========================
private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = White,
    primaryContainer = CreamSecondary,
    onPrimaryContainer = NavyPrimary,

    secondary = BurntOrangeAccent,
    onSecondary = White,
    secondaryContainer = CreamSecondary,
    onSecondaryContainer = BurntOrangeAccentDark,

    tertiary = BurntOrangeAccent,
    onTertiary = White,
    tertiaryContainer = CreamSecondary,
    onTertiaryContainer = BurntOrangeAccentDark,

    background = OffWhiteBg,
    onBackground = CharcoalText,

    surface = Surface,
    onSurface = CharcoalText,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = Destructive,
    onError = DestructiveForeground,
    errorContainer = Color(0xFFFCE4EC),
    onErrorContainer = Destructive,

    outline = WarmGreyBorder,
    outlineVariant = Divider,

    inverseSurface = NavyPrimary,
    inverseOnSurface = CreamSecondary,
    inversePrimary = BurntOrangeAccentLight,

    surfaceTint = NavyPrimary,
    scrim = Black.copy(alpha = 0.32f),
)

// ========================
// Dark Color Scheme
// ========================
private val DarkColorScheme = darkColorScheme(
    primary = CreamSecondary,
    onPrimary = NavyPrimary,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = CreamSecondary,

    secondary = BurntOrangeAccentLight,
    onSecondary = NavyPrimaryDark,
    secondaryContainer = NavyPrimaryDark,
    onSecondaryContainer = BurntOrangeAccentLight,

    tertiary = BurntOrangeAccentLight,
    onTertiary = NavyPrimaryDark,
    tertiaryContainer = NavyPrimary,
    onTertiaryContainer = BurntOrangeAccentLight,

    background = DarkBackground,
    onBackground = DarkForeground,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    error = DarkErrorColor,
    onError = Black,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkErrorColor,

    outline = DarkBorder,
    outlineVariant = DarkDivider,

    inverseSurface = CreamSecondary,
    inverseOnSurface = NavyPrimary,
    inversePrimary = NavyPrimary,

    surfaceTint = CreamSecondary,
    scrim = Black.copy(alpha = 0.5f),
)

// ========================
// Theme Composable
// ========================
@Composable
fun ClimaTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val dimens = WeatherDimens()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 1. Tell the window to draw behind system bars (Edge-to-Edge)
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // 2. Instead of setting manual colors (which are now deprecated),
            // we use the InsetsController to toggle light/dark icons.
            val insetsController = WindowCompat.getInsetsController(window, view)

            // Status bar icons (false = white icons, true = dark icons)
            insetsController.isAppearanceLightStatusBars = !darkTheme

            // Navigation bar icons (false = white icons, true = dark icons)
            insetsController.isAppearanceLightNavigationBars = !darkTheme

            // Note: If you absolutely MUST have a specific background color,
            // it is now recommended to set them via the 'ComponentActivity.enableEdgeToEdge()'
            // call in your MainActivity.kt or by styling your Composables to fill the screen.
        }
    }

    CompositionLocalProvider(
        LocalWeatherExtendedColors provides extendedColors,
        LocalWeatherDimens provides dimens,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WeatherTypography,
            shapes = WeatherShapes,
            content = content,
        )
    }
}

// ========================
// Theme Accessor Extensions
// ========================
object ClimaTrackTheme {
    val extendedColors: WeatherExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalWeatherExtendedColors.current

    val dimens: WeatherDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalWeatherDimens.current
}