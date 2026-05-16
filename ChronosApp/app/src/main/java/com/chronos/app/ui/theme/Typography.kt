package com.chronos.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chronos.app.R

val OrbitronFamily = FontFamily(
    Font(R.font.orbitron_regular,  FontWeight.Normal),
    Font(R.font.orbitron_medium,   FontWeight.Medium),
    Font(R.font.orbitron_bold,     FontWeight.Bold),
    Font(R.font.orbitron_black,    FontWeight.Black),
)

val SpaceMonoFamily = FontFamily(
    Font(R.font.spacemono_regular, FontWeight.Normal),
    Font(R.font.spacemono_bold,    FontWeight.Bold),
)

val RajdhaniFamily = FontFamily(
    Font(R.font.rajdhani_light,    FontWeight.Light),
    Font(R.font.rajdhani_medium,   FontWeight.Medium),
    Font(R.font.rajdhani_bold,     FontWeight.Bold),
)

val ChronosTypography = Typography(
    // Giant clock numerals
    displayLarge = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Black,
        fontSize   = 72.sp,
        letterSpacing = 4.sp
    ),
    // Large clock
    displayMedium = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 48.sp,
        letterSpacing = 3.sp
    ),
    // Medium clock
    displaySmall = TextStyle(
        fontFamily = OrbitronFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 36.sp,
        letterSpacing = 2.sp
    ),
    // Section headers
    headlineLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        letterSpacing = 1.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 18.sp
    ),
    // UI labels
    titleLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 16.sp,
        letterSpacing = 1.sp
    ),
    // Body text
    bodyLarge = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RajdhaniFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp
    ),
    // Monospace labels (ms, timecodes)
    labelLarge = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SpaceMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 11.sp,
        letterSpacing = 1.5.sp
    ),
)
