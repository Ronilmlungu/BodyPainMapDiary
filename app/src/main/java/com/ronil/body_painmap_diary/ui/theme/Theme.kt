package com.ronil.body_painmap_diary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    // Primary — deep navy
    primary              = PrimaryNavy,
    onPrimary            = Color.White,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = Color(0xFFA1BBFF),

    // Secondary — medical blue
    secondary            = SecondaryBlue,
    onSecondary          = Color.White,
    secondaryContainer   = SecondaryFixed,
    onSecondaryContainer = PrimaryNavy,

    // Tertiary — cerulean
    tertiary             = TertiaryCerulean,
    onTertiary           = Color.White,

    // Backgrounds
    background           = AppBackground,
    onBackground         = OnSurface,

    // Surfaces
    surface              = CardSurface,
    onSurface            = OnSurface,
    surfaceVariant       = SurfaceContainer,
    onSurfaceVariant     = OnSurfaceVariant,

    // Borders
    outline              = Outline,
    outlineVariant       = OutlineVariant,

    // Error
    error                = Color(0xFFBA1A1A),
    onError              = Color.White
)

@Composable
fun BodyPainMapDiaryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}