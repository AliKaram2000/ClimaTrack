package com.aeinae.climatrack.ui.theme


import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ========================
// Extended Color System
// ========================
@Immutable
data class WeatherExtendedColors(
    // Hero Section (Home Screen top)
    val heroBackground: Color,
    val heroTextPrimary: Color,
    val heroTextSecondary: Color,

    // Card
    val cardBackground: Color,
    val cardStroke: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textHint: Color,
    val textOnDark: Color,

    // Accent
    val accent: Color,
    val accentLight: Color,
    val accentDark: Color,

    // Input
    val inputBackground: Color,
    val inputBorder: Color,
    val inputBorderFocused: Color,

    // Switch
    val switchTrackOn: Color,
    val switchTrackOff: Color,
    val switchThumb: Color,

    // Navigation
    val bottomNavActive: Color,
    val bottomNavInactive: Color,
    val bottomNavBackground: Color,

    // Divider
    val divider: Color,
    val dividerWarm: Color,

    // Status
    val destructive: Color,
    val destructiveForeground: Color,
    val success: Color,
    val warning: Color,

    // Ripple
    val ripple: Color,

    // Chart
    val chart1: Color,
    val chart2: Color,
    val chart3: Color,
    val chart4: Color,
    val chart5: Color,

    // Surface
    val surfaceElevated: Color,
)

// ========================
// Light Extended Colors
// ========================
val LightExtendedColors = WeatherExtendedColors(
    heroBackground = NavyPrimary,
    heroTextPrimary = CreamSecondary,
    heroTextSecondary = CreamSecondary.copy(alpha = 0.7f),

    cardBackground = CardBackground,
    cardStroke = CardStroke,

    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    textHint = TextHint,
    textOnDark = TextOnDark,

    accent = BurntOrangeAccent,
    accentLight = BurntOrangeAccentLight,
    accentDark = BurntOrangeAccentDark,

    inputBackground = InputBackground,
    inputBorder = InputBorder,
    inputBorderFocused = InputBorderFocused,

    switchTrackOn = SwitchTrackOn,
    switchTrackOff = SwitchTrackOff,
    switchThumb = SwitchThumb,

    bottomNavActive = BottomNavActive,
    bottomNavInactive = NavyPrimaryLight,
    bottomNavBackground = BottomNavBackground,

    divider = Divider,
    dividerWarm = DividerWarm,

    destructive = Destructive,
    destructiveForeground = DestructiveForeground,
    success = Success,
    warning = Warning,

    ripple = RippleLight,

    chart1 = Chart1,
    chart2 = Chart2,
    chart3 = Chart3,
    chart4 = Chart4,
    chart5 = Chart5,

    surfaceElevated = SurfaceElevated,
)

// ========================
// Dark Extended Colors
// ========================
val DarkExtendedColors = WeatherExtendedColors(
    heroBackground = DarkSurfaceElevated,
    heroTextPrimary = DarkTextPrimary,
    heroTextSecondary = DarkTextSecondary,

    cardBackground = DarkCardBackground,
    cardStroke = DarkCardStroke,

    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    textHint = DarkTextMuted.copy(alpha = 0.6f),
    textOnDark = DarkTextPrimary,

    accent = BurntOrangeAccentLight,
    accentLight = BurntOrangeAccentLight,
    accentDark = BurntOrangeAccent,

    inputBackground = DarkInputBackground,
    inputBorder = DarkInputBorder,
    inputBorderFocused = BurntOrangeAccentLight,

    switchTrackOn = BurntOrangeAccent,
    switchTrackOff = DarkSwitchTrackOff,
    switchThumb = SwitchThumb,

    bottomNavActive = BurntOrangeAccentLight,
    bottomNavInactive = DarkBottomNavInactive,
    bottomNavBackground = DarkBottomNavBackground,

    divider = DarkDivider,
    dividerWarm = DarkDivider,

    destructive = DarkErrorColor,
    destructiveForeground = Black,
    success = Success,
    warning = BurntOrangeAccentLight,

    ripple = RippleDark,

    chart1 = DarkChart1,
    chart2 = DarkChart2,
    chart3 = DarkChart3,
    chart4 = DarkChart4,
    chart5 = DarkChart5,

    surfaceElevated = DarkSurfaceElevated,
)

val LocalWeatherExtendedColors = staticCompositionLocalOf { LightExtendedColors }