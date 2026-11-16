package com.tharun.mymusic.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Dark Theme Colors
val CharcoalDark = Color(0xFF0A0A0D)
val CharcoalSurface = Color(0xFF131318)
val CharcoalSurfaceVariant = Color(0xFF1C1C24)
val CharcoalCard = Color(0xFF17171F)
val CharcoalBorder = Color(0xFF262633)

val AccentPrimary = Color(0xFFFF3B5C)
val AccentSecondary = Color(0xFFFF6B8B)
val AccentTertiary = Color(0xFF7928CA)
val AccentCyan = Color(0xFF00E5FF)
val AccentGreen = Color(0xFF10B981)

val TextPrimaryDark = Color(0xFFF5F5F7)
val TextSecondaryDark = Color(0xFFA1A1AA)
val TextMutedDark = Color(0xFF71717A)

// Light Theme Colors (Optional Setting)
val LightBackground = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F3F5)
val LightCard = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE4E7EB)

val TextPrimaryLight = Color(0xFF18181B)
val TextSecondaryLight = Color(0xFF52525B)
val TextMutedLight = Color(0xFFA1A1AA)

// Brand Gradients
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(AccentPrimary, Color(0xFFFF6584), AccentTertiary)
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(AccentPrimary.copy(alpha = 0.35f), Color.Transparent)
)

val GlassSurfaceGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF252533).copy(alpha = 0.7f),
        Color(0xFF171720).copy(alpha = 0.85f)
    )
)
