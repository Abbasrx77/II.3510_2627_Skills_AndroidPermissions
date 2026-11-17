package fr.isep.permdemo.demos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.permissions.PermissionStatus
import fr.isep.permdemo.permissions.rememberPermissionFlow
import fr.isep.permdemo.ui.components.ResultCard
import fr.isep.permdemo.ui.components.StatusChip
import fr.isep.permdemo.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun LocationDemo(case: DemoCase, flow: PermissionFlow) {
    val context = LocalContext.current
    var result by remember { mutableStateOf<String?>(null) }

    PermissionButton(flow, stringResource(R.string.location_show), case) {
        val precision = if (flow.status == PermissionStatus.APPROXIMATE) "Approximative" else "Précise"
        flow.log.step(5, "Précision choisie : $precision")
        if (!isLocationEnabled(context)) {
            result = context.getString(R.string.location_disabled)
            flow.log.step(5, "isLocationEnabled() → false : localisation désactivée sur l'appareil")
            return@PermissionButton
        }
        result = context.getString(R.string.location_searching)
        readLastLocation(context) { location ->
            result = describe(context, location)
            flow.log.step(5, "LocationManager → ${location?.let { coordinates(it) } ?: "aucune position"}")
        }
    }
    if (flow.status == PermissionStatus.GRANTED || flow.status == PermissionStatus.APPROXIMATE) {
        val precision = if (flow.status == PermissionStatus.APPROXIMATE) R.string.location_approximate else R.string.location_precise
        Text(stringResource(precision), style = MaterialTheme.typography.titleMedium)
    }
    result?.let { ResultCard(it) }

    BackgroundLocation(case, foregroundGranted = flow.status.canUseFeature)
}

@Composable
private fun BackgroundLocation(case: DemoCase, foregroundGranted: Boolean) {
    val backgroundCase = remember(case) {
        case.copy(permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), minSdk = 29)
    }
    val note = if (Build.VERSION.SDK_INT >= 30) "Android 11+ : le système ouvre les Paramètres, pas de boîte de dialogue" else null
    val background = rememberPermissionFlow(backgroundCase, requestNote = note)

    PermissionButton(background, stringResource(R.string.location_background), case, enabled = foregroundGranted) {
        background.log.step(5, "ACCESS_BACKGROUND_LOCATION accordée : position possible app fermée")
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.location_background_label), style = MaterialTheme.typography.titleSmall)
        StatusChip(background.status)
    }
    Text(stringResource(R.string.location_background_hint), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
}

private fun isLocationEnabled(context: Context): Boolean =
    LocationManagerCompat.isLocationEnabled(context.getSystemService(LocationManager::class.java))

@SuppressLint("MissingPermission")
private fun readLastLocation(context: Context, onResult: (Location?) -> Unit) {
    val manager = context.getSystemService(LocationManager::class.java)
    val providers = manager.getProviders(true)
    val last = providers.mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
        .maxByOrNull { it.time }
    if (last != null) return onResult(last)

    val provider = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER, "fused")
        .firstOrNull { it in providers } ?: return onResult(null)
    val noCancel: CancellationSignal? = null
    LocationManagerCompat.getCurrentLocation(manager, provider, noCancel, ContextCompat.getMainExecutor(context)) {
        onResult(it)
    }
}

private fun describe(context: Context, location: Location?): String =
    if (location == null) {
        context.getString(R.string.location_unavailable)
    } else {
        context.getString(
            R.string.location_result,
            coordinates(location),
            location.accuracy.toInt(),
            location.provider.orEmpty(),
        )
    }

private fun coordinates(location: Location): String =
    String.format(Locale.US, "%.5f, %.5f", location.latitude, location.longitude)
