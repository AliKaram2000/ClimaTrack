package com.aeinae.climatrack.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class WeatherDimens(
    // Spacing
    val spacing2: Dp = 2.dp,
    val spacing4: Dp = 4.dp,
    val spacing8: Dp = 8.dp,
    val spacing12: Dp = 12.dp,
    val spacing16: Dp = 16.dp,
    val spacing20: Dp = 20.dp,
    val spacing24: Dp = 24.dp,
    val spacing32: Dp = 32.dp,
    val spacing48: Dp = 48.dp,

    // Border Radius
    val radiusSm: Dp = 6.dp,
    val radiusMd: Dp = 8.dp,
    val radiusLg: Dp = 10.dp,
    val radiusXl: Dp = 14.dp,

    // Typography Sizes
    val textXs: TextUnit = 12.sp,
    val textSm: TextUnit = 14.sp,
    val textBase: TextUnit = 16.sp,
    val textLg: TextUnit = 18.sp,
    val textXl: TextUnit = 20.sp,
    val text2xl: TextUnit = 24.sp,
    val text3xl: TextUnit = 30.sp,
    val text4xl: TextUnit = 36.sp,
    val textDisplay: TextUnit = 72.sp,

    // Card
    val cardElevation: Dp = 2.dp,
    val cardPadding: Dp = 12.dp,
    val cardMargin: Dp = 12.dp,
    val cardStrokeWidth: Dp = 1.dp,

    // FAB
    val fabSize: Dp = 56.dp,
    val fabMargin: Dp = 16.dp,

    // Bottom Navigation
    val bottomNavHeight: Dp = 56.dp,
    val bottomNavIconSize: Dp = 24.dp,

    // App Bar
    val appBarHeight: Dp = 56.dp,

    // Row Heights
    val listItemHeight: Dp = 48.dp,
    val forecastRowHeight: Dp = 56.dp,

    // Divider
    val dividerHeight: Dp = 1.dp,
    val hairlineHeight: Dp = 0.5.dp,

    // Icon Sizes
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 24.dp,
    val iconLg: Dp = 32.dp,
    val iconXl: Dp = 48.dp,
    val weatherIconLarge: Dp = 64.dp,
)

val LocalWeatherDimens = staticCompositionLocalOf { WeatherDimens() }