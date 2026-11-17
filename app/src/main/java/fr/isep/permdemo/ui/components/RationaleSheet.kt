package fr.isep.permdemo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.SurfaceWhite
import fr.isep.permdemo.ui.theme.TextSecondary
import fr.isep.permdemo.ui.theme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RationaleSheet(case: DemoCase, onContinue: () -> Unit, onDismiss: () -> Unit) {
    val colors = case.type.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceWhite,
    ) {
        Column(Modifier.padding(horizontal = 24.dp).navigationBarsPadding().padding(bottom = 16.dp)) {
            ExpressiveShape(case.shape, colors.strong, 56.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.rationale_title, stringResource(case.title)),
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
            )
            Spacer(Modifier.height(8.dp))
            Text(stringResource(case.rationale), style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            Spacer(Modifier.height(24.dp))
            PillButton(stringResource(R.string.rationale_continue), onContinue, containerColor = colors.strong)
            Spacer(Modifier.height(8.dp))
            OutlinedPillButton(stringResource(R.string.rationale_not_now), onDismiss)
        }
    }
}
