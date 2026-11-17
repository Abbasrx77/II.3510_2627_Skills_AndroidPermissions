package fr.isep.permdemo.demos

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.permissions.PermissionStatus
import fr.isep.permdemo.ui.components.PillButton
import fr.isep.permdemo.ui.components.RationaleSheet
import fr.isep.permdemo.ui.theme.colors

@Composable
fun PermissionButton(
    flow: PermissionFlow,
    label: String,
    case: DemoCase,
    enabled: Boolean = true,
    onGranted: () -> Unit,
) {
    if (flow.status == PermissionStatus.PERMANENTLY_DENIED) {
        PillButton(stringResource(R.string.action_open_settings), flow::openAppSettings, enabled = enabled)
    } else {
        PillButton(
            text = label,
            onClick = { flow.start(onGranted) },
            containerColor = case.type.colors.strong,
            enabled = enabled,
        )
    }
    if (flow.rationaleVisible) {
        RationaleSheet(case, onContinue = flow::onRationaleAccepted, onDismiss = flow::onRationaleDismissed)
    }
}
