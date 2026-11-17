package fr.isep.permdemo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.isep.permdemo.R

val BricolageGrotesque = FontFamily(
    Font(R.font.bricolage_grotesque_bold, FontWeight.Bold),
    Font(R.font.bricolage_grotesque_extrabold, FontWeight.ExtraBold),
)

val Figtree = FontFamily(
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
    Font(R.font.figtree_bold, FontWeight.Bold),
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold),
)

val CodeTextStyle = TextStyle(fontFamily = JetBrainsMono, fontSize = 12.sp, lineHeight = 18.sp)
val ConstantTextStyle = TextStyle(fontFamily = JetBrainsMono, fontSize = 12.sp, lineHeight = 16.sp)

private fun display(size: Int, weight: FontWeight = FontWeight.ExtraBold) = TextStyle(
    fontFamily = BricolageGrotesque,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * 1.1f).sp,
    letterSpacing = (-0.5).sp,
)

private fun figtree(size: Int, weight: FontWeight, lineHeight: Int) = TextStyle(
    fontFamily = Figtree,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

val Typography = Typography(
    displayLarge = display(52),
    displayMedium = display(44),
    displaySmall = display(36),
    headlineLarge = display(32),
    headlineMedium = display(28),
    headlineSmall = display(24, FontWeight.Bold),
    titleLarge = display(22, FontWeight.Bold),
    titleMedium = figtree(16, FontWeight.SemiBold, 22),
    titleSmall = figtree(14, FontWeight.SemiBold, 20),
    bodyLarge = figtree(16, FontWeight.Normal, 24),
    bodyMedium = figtree(14, FontWeight.Normal, 20),
    bodySmall = figtree(12, FontWeight.Normal, 16),
    labelLarge = figtree(16, FontWeight.SemiBold, 20),
    labelMedium = figtree(13, FontWeight.SemiBold, 16),
    labelSmall = figtree(11, FontWeight.Bold, 14),
)
