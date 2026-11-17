package fr.isep.permdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = SurfaceWhite,
    primaryContainer = GrantedContainer,
    onPrimaryContainer = Ink,
    secondary = AndroidGreen,
    onSecondary = Ink,
    background = Background,
    onBackground = Ink,
    surface = SurfaceWhite,
    onSurface = Ink,
    surfaceVariant = Background,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = SurfaceWhite,
    surfaceContainerLow = SurfaceWhite,
    surfaceContainer = SurfaceWhite,
    surfaceContainerHigh = SurfaceWhite,
    surfaceContainerHighest = SurfaceWhite,
    outline = Border,
    outlineVariant = Border,
)

@Composable
fun PermDemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
