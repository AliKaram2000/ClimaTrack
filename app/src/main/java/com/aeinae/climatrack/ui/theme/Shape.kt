package com.aeinae.climatrack.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val WeatherShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),       // radius_sm
    medium = RoundedCornerShape(8.dp),      // radius_md
    large = RoundedCornerShape(10.dp),      // radius_lg
    extraLarge = RoundedCornerShape(14.dp), // radius_xl
)

// Additional custom shapes
val BottomSheetShape = RoundedCornerShape(
    topStart = 14.dp,
    topEnd = 14.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp,
)

val PillShape = RoundedCornerShape(50)

val CardShape = RoundedCornerShape(8.dp)

val ChipShape = RoundedCornerShape(6.dp)