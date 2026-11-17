package fr.isep.permdemo.demos

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.ui.components.PhotoResult

@Composable
fun CameraDemo(case: DemoCase, flow: PermissionFlow) {
    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        thumbnail = bitmap
        val result = if (bitmap != null) "miniature ${bitmap.width}×${bitmap.height}" else "annulé"
        flow.log.step(5, "TakePicturePreview → $result")
    }

    PermissionButton(flow, stringResource(R.string.camera_take_photo), case) {
        flow.log.step(5, "CAMERA accordée → l'Intent caméra est autorisé (sinon SecurityException)")
        takePicture.launch(null)
    }

    thumbnail?.let { PhotoResult(it.asImageBitmap(), stringResource(R.string.camera_thumbnail), fillWidth = false) }
}
