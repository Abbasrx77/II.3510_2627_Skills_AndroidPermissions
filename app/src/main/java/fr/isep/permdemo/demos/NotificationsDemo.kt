package fr.isep.permdemo.demos

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.system.NotificationHelper
import fr.isep.permdemo.ui.components.ResultCard
import fr.isep.permdemo.ui.theme.TextSecondary

@Composable
fun NotificationsDemo(case: DemoCase, flow: PermissionFlow) {
    val context = LocalContext.current
    var sentCount by remember { mutableIntStateOf(0) }

    PermissionButton(flow, stringResource(R.string.notif_send), case) {
        val sent = NotificationHelper.show(
            context,
            TEST_NOTIFICATION_ID,
            context.getString(R.string.notif_test_title),
            context.getString(R.string.notif_test_text),
        )
        flow.log.step(5, if (sent) "NotificationManagerCompat.notify() → envoyée" else "Notifications bloquées dans les Paramètres")
        if (sent) sentCount++
    }

    if (Build.VERSION.SDK_INT < 33) {
        Text(
            stringResource(R.string.notif_not_needed, Build.VERSION.SDK_INT),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
    if (sentCount > 0) ResultCard(stringResource(R.string.notif_sent, sentCount))
}

private const val TEST_NOTIFICATION_ID = 3
