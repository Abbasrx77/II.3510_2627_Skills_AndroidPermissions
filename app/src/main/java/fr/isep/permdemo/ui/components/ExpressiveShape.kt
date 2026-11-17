package fr.isep.permdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@Composable
fun ExpressiveShape(shape: Shape, color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size).background(color, shape))
}
