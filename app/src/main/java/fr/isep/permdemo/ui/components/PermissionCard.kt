package fr.isep.permdemo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionStatus
import fr.isep.permdemo.ui.theme.CardShape
import fr.isep.permdemo.ui.theme.ConstantTextStyle
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.TextSecondary
import fr.isep.permdemo.ui.theme.colors

@Composable
fun PermissionCard(
    case: DemoCase,
    status: PermissionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = case.type.colors
    Box(
        modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(colors.container)
            .clickable(onClick = onClick),
    ) {
        ExpressiveShape(
            shape = case.shape,
            color = colors.strong.copy(alpha = 0.18f),
            size = 120.dp,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 28.dp, y = (-20).dp),
        )
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(case.type.label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.strong,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(case.title),
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
            )
            Spacer(Modifier.height(2.dp))
            if (case.constants.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_constant),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            } else {
                Text(
                    text = case.constants.joinToString(" · "),
                    style = ConstantTextStyle,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.height(14.dp))
            Row { StatusChip(status) }
        }
    }
}
