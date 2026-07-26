package com.example.museumgame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DayGalleryColorScheme = lightColorScheme(
    primary = DayGalleryColors.PrimaryAccent,
    onPrimary = DayGalleryColors.Surface,
    primaryContainer = DayGalleryColors.StrongAccent,
    onPrimaryContainer = DayGalleryColors.Surface,
    secondary = DayGalleryColors.Brass,
    onSecondary = DayGalleryColors.Surface,
    secondaryContainer = DayGalleryColors.RaisedSurface,
    onSecondaryContainer = DayGalleryColors.PrimaryText,
    tertiary = DayGalleryColors.Success,
    onTertiary = DayGalleryColors.Surface,
    tertiaryContainer = DayGalleryColors.RaisedSurface,
    onTertiaryContainer = DayGalleryColors.Success,
    background = DayGalleryColors.Background,
    onBackground = DayGalleryColors.PrimaryText,
    surface = DayGalleryColors.Surface,
    onSurface = DayGalleryColors.PrimaryText,
    surfaceVariant = DayGalleryColors.RaisedSurface,
    onSurfaceVariant = DayGalleryColors.SecondaryText,
    surfaceTint = DayGalleryColors.PrimaryAccent,
    inverseSurface = DayGalleryColors.PrimaryText,
    inverseOnSurface = DayGalleryColors.Background,
    inversePrimary = AfterHoursColors.PrimaryAccent,
    error = DayGalleryColors.Destructive,
    onError = DayGalleryColors.Surface,
    errorContainer = DayGalleryColors.RaisedSurface,
    onErrorContainer = DayGalleryColors.Destructive,
    outline = DayGalleryColors.Outline,
    outlineVariant = DayGalleryColors.Outline,
    scrim = Color(0x99000000),
    surfaceBright = DayGalleryColors.Surface,
    surfaceDim = DayGalleryColors.Background,
    surfaceContainer = DayGalleryColors.RaisedSurface,
    surfaceContainerHigh = DayGalleryColors.RaisedSurface,
    surfaceContainerHighest = DayGalleryColors.RaisedSurface,
    surfaceContainerLow = DayGalleryColors.Surface,
    surfaceContainerLowest = DayGalleryColors.Surface
)

private val MuseumAfterHoursColorScheme = darkColorScheme(
    primary = AfterHoursColors.PrimaryAccent,
    onPrimary = AfterHoursColors.Background,
    primaryContainer = AfterHoursColors.StrongAccent,
    onPrimaryContainer = AfterHoursColors.Background,
    secondary = AfterHoursColors.Brass,
    onSecondary = AfterHoursColors.Background,
    secondaryContainer = AfterHoursColors.RaisedSurface,
    onSecondaryContainer = AfterHoursColors.PrimaryText,
    tertiary = AfterHoursColors.Success,
    onTertiary = AfterHoursColors.Background,
    tertiaryContainer = AfterHoursColors.RaisedSurface,
    onTertiaryContainer = AfterHoursColors.Success,
    background = AfterHoursColors.Background,
    onBackground = AfterHoursColors.PrimaryText,
    surface = AfterHoursColors.Surface,
    onSurface = AfterHoursColors.PrimaryText,
    surfaceVariant = AfterHoursColors.RaisedSurface,
    onSurfaceVariant = AfterHoursColors.SecondaryText,
    surfaceTint = AfterHoursColors.PrimaryAccent,
    inverseSurface = AfterHoursColors.PrimaryText,
    inverseOnSurface = AfterHoursColors.Background,
    inversePrimary = DayGalleryColors.PrimaryAccent,
    error = AfterHoursColors.Destructive,
    onError = AfterHoursColors.Background,
    errorContainer = AfterHoursColors.RaisedSurface,
    onErrorContainer = AfterHoursColors.Destructive,
    outline = AfterHoursColors.Outline,
    outlineVariant = AfterHoursColors.Outline,
    scrim = Color(0xB3000000),
    surfaceBright = AfterHoursColors.RaisedSurface,
    surfaceDim = AfterHoursColors.Background,
    surfaceContainer = AfterHoursColors.RaisedSurface,
    surfaceContainerHigh = AfterHoursColors.RaisedSurface,
    surfaceContainerHighest = AfterHoursColors.RaisedSurface,
    surfaceContainerLow = AfterHoursColors.Surface,
    surfaceContainerLowest = AfterHoursColors.Surface
)

@Composable
fun MuseumGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            MuseumAfterHoursColorScheme
        } else {
            DayGalleryColorScheme
        },
        typography = MuseumTypography,
        content = content
    )
}
