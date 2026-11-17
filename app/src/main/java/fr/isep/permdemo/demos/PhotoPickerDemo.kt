package fr.isep.permdemo.demos

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.permissions.PermissionType
import fr.isep.permdemo.permissions.rememberPermissionFlow
import fr.isep.permdemo.ui.components.EventLog
import fr.isep.permdemo.ui.components.PhotoResult
import fr.isep.permdemo.ui.components.PillButton
import fr.isep.permdemo.ui.components.ResultCard
import fr.isep.permdemo.ui.components.StatusChip
import fr.isep.permdemo.ui.theme.Ink
import fr.isep.permdemo.ui.theme.TextSecondary
import fr.isep.permdemo.ui.theme.colors

@Composable
fun PhotoPickerDemo(case: DemoCase, flow: PermissionFlow) {
    val context = LocalContext.current
    val log = flow.log
    var photo by remember { mutableStateOf<ImageBitmap?>(null) }

    // TODO 6: Register a PickVisualMedia launcher. When a photo comes back, log steps 2 and 3 (with the received URI), set photo = loadThumbnail(context, uri), then log step 4.

    PillButton(
        text = stringResource(R.string.photo_pick),
        onClick = {
            log.step(0, "Rien à déclarer dans le Manifest")
            log.step(1, "PickVisualMedia(ImageOnly) → sélecteur du système, aucune boîte de dialogue")
            log.waitFor(2)
            // TODO 6: Launch the picker for images only (PickVisualMediaRequest with PickVisualMedia.ImageOnly).
        },
        containerColor = case.type.colors.strong,
    )
    photo?.let { PhotoResult(it, stringResource(R.string.photo_picked)) }
    Text(stringResource(R.string.photo_no_permission_hint), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

    WithPermissionComparison(case)
}

@Composable
private fun WithPermissionComparison(case: DemoCase) {
    val context = LocalContext.current
    val comparisonCase = remember(case) {
        case.copy(id = "photo_with_permission", type = PermissionType.DANGEROUS)
    }
    val flow = rememberPermissionFlow(comparisonCase)
    var visibleCount by remember { mutableStateOf<Int?>(null) }
    var latest by remember { mutableStateOf<ImageBitmap?>(null) }

    Text(stringResource(R.string.photo_compare_title), style = MaterialTheme.typography.titleLarge, color = Ink)
    PermissionButton(flow, stringResource(R.string.photo_compare_button), comparisonCase) {
        val images = queryImages(context)
        visibleCount = images.size
        latest = images.firstOrNull()?.let { loadThumbnail(context, it) }
        flow.log.step(5, "MediaStore.Images → ${images.size} photo(s) visible(s)")
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.photo_compare_status), style = MaterialTheme.typography.titleSmall)
        StatusChip(flow.status)
    }
    visibleCount?.let { ResultCard(pluralStringResource(R.plurals.photo_visible_count, it, it)) }
    latest?.let { PhotoResult(it, stringResource(R.string.photo_picked)) }
    Text(stringResource(R.string.photo_compare_message), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    if (flow.log.events.isNotEmpty()) EventLog(flow.log)
}

private fun queryImages(context: Context): List<Uri> {
    val images = mutableListOf<Uri>()
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.Media._ID),
        null,
        null,
        "${MediaStore.Images.Media.DATE_ADDED} DESC",
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            images += ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(0))
        }
    }
    return images
}

private fun loadThumbnail(context: Context, uri: Uri): ImageBitmap? = runCatching {
    if (Build.VERSION.SDK_INT >= 29) {
        context.contentResolver.loadThumbnail(uri, Size(1024, 1024), null)
    } else {
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = 4 })
        }
    }
}.getOrNull()?.asImageBitmap()
