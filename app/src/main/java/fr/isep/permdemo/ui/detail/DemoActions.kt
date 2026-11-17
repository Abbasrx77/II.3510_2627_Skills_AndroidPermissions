package fr.isep.permdemo.ui.detail

import androidx.compose.runtime.Composable
import fr.isep.permdemo.demos.CameraDemo
import fr.isep.permdemo.demos.ExactAlarmDemo
import fr.isep.permdemo.demos.LocationDemo
import fr.isep.permdemo.demos.MicDemo
import fr.isep.permdemo.demos.NotificationsDemo
import fr.isep.permdemo.demos.PhotoPickerDemo
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow

@Composable
fun DemoActions(case: DemoCase, flow: PermissionFlow) {
    when (case.id) {
        "camera" -> CameraDemo(case, flow)
        "location" -> LocationDemo(case, flow)
        "notifications" -> NotificationsDemo(case, flow)
        "photo_picker" -> PhotoPickerDemo(case, flow)
        "mic" -> MicDemo(case, flow)
        "exact_alarm" -> ExactAlarmDemo(case, flow)
    }
}
