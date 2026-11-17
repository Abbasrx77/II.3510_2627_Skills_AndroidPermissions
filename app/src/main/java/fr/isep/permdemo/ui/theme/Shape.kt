package fr.isep.permdemo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

val CardShape = RoundedCornerShape(28.dp)
val CodeCardShape = RoundedCornerShape(20.dp)
val PillShape = RoundedCornerShape(percent = 50)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = CardShape,
    extraLarge = CardShape,
)

class PolarShape(
    private val lobes: Int,
    private val amplitude: Float,
    private val rotationDegrees: Float = 0f,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseRadius = min(cx, cy) / (1f + amplitude)
        val rotation = rotationDegrees * PI.toFloat() / 180f
        val path = Path()
        for (i in 0 until POINTS) {
            val theta = 2f * PI.toFloat() * i / POINTS
            val r = baseRadius * (1f + amplitude * cos(lobes * theta))
            val x = cx + r * cos(theta + rotation)
            val y = cy + r * sin(theta + rotation)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }

    private companion object {
        const val POINTS = 180
    }
}

object ExpressiveShapes {
    val Cookie = PolarShape(lobes = 9, amplitude = 0.055f)
    val Clover = PolarShape(lobes = 4, amplitude = 0.2f, rotationDegrees = 45f)
    val SoftBurst = PolarShape(lobes = 12, amplitude = 0.085f)
    val Flower = PolarShape(lobes = 6, amplitude = 0.13f)
    val Sunny = PolarShape(lobes = 16, amplitude = 0.05f)
}
