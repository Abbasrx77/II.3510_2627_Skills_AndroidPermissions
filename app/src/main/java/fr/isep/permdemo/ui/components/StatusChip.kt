package fr.isep.permdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.permissions.PermissionStatus
import fr.isep.permdemo.ui.theme.AndroidGreen
import fr.isep.permdemo.ui.theme.Background
import fr.isep.permdemo.ui.theme.Border
import fr.isep.permdemo.ui.theme.GrantedContainer
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.PillShape
import fr.isep.permdemo.ui.theme.SurfaceWhite
import fr.isep.permdemo.ui.theme.TextSecondary

private data class ChipStyle(val container: Color, val text: Color, val dot: Color, val outlined: Boolean)

private fun PermissionStatus.style(): ChipStyle = when (this) {
    PermissionStatus.NEVER_ASKED -> ChipStyle(SurfaceWhite, TextSecondary, Border, outlined = true)
    PermissionStatus.GRANTED,
    PermissionStatus.APPROXIMATE,
    PermissionStatus.PARTIAL -> ChipStyle(GrantedContainer, Ink, AndroidGreen, outlined = false)
    PermissionStatus.DENIED -> ChipStyle(Color(0xFFFFE9A8), Ink, Color(0xFF7A5400), outlined = false)
    PermissionStatus.PERMANENTLY_DENIED -> ChipStyle(Ink, SurfaceWhite, Color(0xFFFF8A65), outlined = false)
    PermissionStatus.NOT_NEEDED -> ChipStyle(Background, TextSecondary, Border, outlined = true)
}

@Composable
fun StatusChip(status: PermissionStatus, modifier: Modifier = Modifier) {
    val style = status.style()
    Row(
        modifier = modifier
            .background(style.container, PillShape)
            .then(if (style.outlined) Modifier.border(1.dp, Border, PillShape) else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(8.dp).background(style.dot, CircleShape))
        Text(
            text = stringResource(status.label),
            style = MaterialTheme.typography.labelMedium,
            color = style.text,
        )
    }
}
