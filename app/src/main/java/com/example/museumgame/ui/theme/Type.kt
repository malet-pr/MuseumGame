package com.example.museumgame.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.museumgame.R

private val MuseumHeadingFont = FontFamily(
    Font(
        resId = R.font.cormorant_garamond,
        weight = FontWeight.SemiBold
    )
)

private val MuseumBodyFont = FontFamily(
    Font(
        resId = R.font.source_sans_3_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.source_sans_3_semibold,
        weight = FontWeight.SemiBold
    )
)

private fun headingStyle(
    fontSize: Int,
    lineHeight: Int
) = TextStyle(
    fontFamily = MuseumHeadingFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp
)

private fun bodyStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal
) = TextStyle(
    fontFamily = MuseumBodyFont,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp
)

val MuseumTypography = Typography(
    displayLarge = headingStyle(fontSize = 36, lineHeight = 40),
    displayMedium = headingStyle(fontSize = 34, lineHeight = 38),
    displaySmall = headingStyle(fontSize = 32, lineHeight = 36),
    headlineLarge = headingStyle(fontSize = 30, lineHeight = 34),
    headlineMedium = headingStyle(fontSize = 23, lineHeight = 28),
    headlineSmall = headingStyle(fontSize = 22, lineHeight = 26),
    titleLarge = bodyStyle(
        fontSize = 22,
        lineHeight = 28,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = bodyStyle(
        fontSize = 18,
        lineHeight = 24,
        fontWeight = FontWeight.SemiBold
    ),
    titleSmall = bodyStyle(
        fontSize = 16,
        lineHeight = 22,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = bodyStyle(fontSize = 18, lineHeight = 26),
    bodyMedium = bodyStyle(fontSize = 17, lineHeight = 24),
    bodySmall = bodyStyle(fontSize = 15, lineHeight = 21),
    labelLarge = bodyStyle(
        fontSize = 16,
        lineHeight = 20,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = bodyStyle(
        fontSize = 15,
        lineHeight = 20,
        fontWeight = FontWeight.SemiBold
    ),
    labelSmall = bodyStyle(
        fontSize = 14,
        lineHeight = 18,
        fontWeight = FontWeight.SemiBold
    )
)
