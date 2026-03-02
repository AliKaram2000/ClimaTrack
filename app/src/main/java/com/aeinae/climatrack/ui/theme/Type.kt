package com.aeinae.climatrack.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aeinae.climatrack.R


// ========================
// Font Families
// ========================
val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_medium, FontWeight.Medium),
    Font(R.font.playfair_display_semibold, FontWeight.SemiBold),
    Font(R.font.playfair_display_bold, FontWeight.Bold),
    Font(R.font.playfair_display_italic, FontWeight.Normal, FontStyle.Italic),
)

val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

// ========================
// Custom Text Styles
// ========================

// Display - Temperature large number
val DisplayLarge = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Normal,
    fontSize = 72.sp,
    letterSpacing = (-0.5).sp,
)

// Serif Headline Styles
val HeadlineLargeSerif = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.SemiBold,
    fontSize = 30.sp,
    letterSpacing = (-0.25).sp,
)

val HeadlineMediumSerif = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
)

val HeadlineSmallSerif = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
)

val TitleLargeSerif = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
)

val TitleMediumSerif = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
)

// Description italic
val DescriptionItalic = TextStyle(
    fontFamily = PlayfairDisplay,
    fontWeight = FontWeight.Normal,
    fontStyle = FontStyle.Italic,
    fontSize = 16.sp,
)

// Section header style (UPPERCASE muted label)
val SectionHeader = TextStyle(
    fontFamily = Inter,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    letterSpacing = 1.sp,
)

// ========================
// Material 3 Typography
// ========================
val WeatherTypography = Typography(
    // Display
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 72.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        letterSpacing = (-0.25).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
    ),

    // Headline (Serif)
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    ),

    // Title (Serif)
    titleLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
    ),

    // Body (Sans-serif)
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    ),

    // Label (Sans-serif)
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.8.sp,
    ),
)