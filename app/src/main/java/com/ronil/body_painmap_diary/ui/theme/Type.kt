package com.ronil.body_painmap_diary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ronil.body_painmap_diary.R

// 1. Define the Headline Family (Plus Jakarta Sans)
val JakartaSans = FontFamily(
    Font(R.font.jakarta_regular, FontWeight.Normal),
    Font(R.font.jakarta_medium, FontWeight.Medium),
    Font(R.font.jakarta_semibold, FontWeight.SemiBold),
    Font(R.font.jakarta_bold, FontWeight.Bold)
)

// 2. Define the Body Family (Atkinson Hyperlegible)
val AtkinsonHyperlegible = FontFamily(
    Font(R.font.atkinson_regular, FontWeight.Normal),
    Font(R.font.atkinson_bold, FontWeight.Bold)
)

// 3. Apply them to the App Typography
val AppTypography = Typography(
    // --- Headlines use Jakarta Sans (friendly, geometric) ---
    headlineLarge  = TextStyle(fontFamily = JakartaSans, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = JakartaSans, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp),
    headlineSmall  = TextStyle(fontFamily = JakartaSans, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),

    titleLarge     = TextStyle(fontFamily = JakartaSans, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontFamily = JakartaSans, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),

    // --- Body & Labels use Atkinson (maximum legibility for elderly) ---
    bodyLarge      = TextStyle(fontFamily = AtkinsonHyperlegible, fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 26.sp),
    bodyMedium     = TextStyle(fontFamily = AtkinsonHyperlegible, fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
    bodySmall      = TextStyle(fontFamily = AtkinsonHyperlegible, fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),

    labelLarge     = TextStyle(fontFamily = AtkinsonHyperlegible, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp),
    labelMedium    = TextStyle(fontFamily = AtkinsonHyperlegible, fontSize = 14.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
)