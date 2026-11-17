package fr.isep.permdemo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.ui.theme.Border
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.PillShape
import fr.isep.permdemo.ui.theme.SurfaceWhite

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Ink,
    contentColor: Color = SurfaceWhite,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        elevation = null,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun OutlinedPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Ink,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        border = BorderStroke(1.dp, Border),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite, contentColor = contentColor),
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
