package fr.isep.permdemo.permissions

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Shape
import fr.isep.permdemo.R
import fr.isep.permdemo.ui.theme.ExpressiveShapes

enum class PermissionType(@StringRes val label: Int, @StringRes val whoDecides: Int) {
    NORMAL(R.string.type_normal, R.string.who_decides_normal),
    SIGNATURE(R.string.type_signature, R.string.who_decides_signature),
    DANGEROUS(R.string.type_dangerous, R.string.who_decides_dangerous),
    SPECIAL(R.string.type_special, R.string.who_decides_special),
    NONE(R.string.type_none, R.string.who_decides_none),
}

data class LifecycleStep(val number: Int, @StringRes val title: Int, val api: String?)

data class TouchedFile(val path: String, @StringRes val role: Int, val code: String)

data class DemoCase(
    val id: String,
    @StringRes val title: Int,
    val type: PermissionType,
    val constants: List<String>,
    val shape: Shape,
    @StringRes val rationale: Int,
    @StringRes val systemPath: Int,
    val files: List<TouchedFile>,
    val permissions: List<String> = emptyList(),
    val minSdk: Int = 1,
    val partialStatus: PermissionStatus = PermissionStatus.GRANTED,
)

fun DemoCase.readStatus(activity: Activity, prefs: PermissionPrefs): PermissionStatus = when (type) {
    PermissionType.NONE -> PermissionStatus.NOT_NEEDED
    PermissionType.SPECIAL -> readExactAlarmStatus(activity, prefs, permissions.first())
    else -> readStatus(activity, permissions, prefs, partialStatus, minSdk)
}

fun photoPermissions(): List<String> = when {
    Build.VERSION.SDK_INT >= 34 -> listOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
    )
    Build.VERSION.SDK_INT >= 33 -> listOf(Manifest.permission.READ_MEDIA_IMAGES)
    else -> listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}

val PermissionType.lifecycle: List<LifecycleStep>
    get() = when (this) {
        PermissionType.SPECIAL -> SPECIAL_STEPS
        PermissionType.NONE -> NO_PERMISSION_STEPS
        else -> DANGEROUS_STEPS
    }

private val DANGEROUS_STEPS = listOf(
    LifecycleStep(0, R.string.step_declared, "<uses-permission>"),
    LifecycleStep(1, R.string.step_check, "checkSelfPermission"),
    LifecycleStep(2, R.string.step_explain, "shouldShowRequestPermissionRationale"),
    LifecycleStep(3, R.string.step_request, "launcher.launch"),
    LifecycleStep(4, R.string.step_handle, "onResult(Map<String, Boolean>)"),
    LifecycleStep(5, R.string.step_use, "ACTION_APPLICATION_DETAILS_SETTINGS"),
)

private val SPECIAL_STEPS = listOf(
    LifecycleStep(0, R.string.step_declared, "<uses-permission>"),
    LifecycleStep(1, R.string.step_check, "canScheduleExactAlarms"),
    LifecycleStep(2, R.string.step_explain_sheet, "RationaleSheet"),
    LifecycleStep(3, R.string.step_open_settings, "ACTION_REQUEST_SCHEDULE_EXACT_ALARM"),
    LifecycleStep(4, R.string.step_back_in_app, "ON_RESUME"),
    LifecycleStep(5, R.string.step_schedule_alarm, "setExactAndAllowWhileIdle"),
)

private val NO_PERMISSION_STEPS = listOf(
    LifecycleStep(0, R.string.step_nothing_to_declare, null),
    LifecycleStep(1, R.string.step_open_picker, "PickVisualMedia"),
    LifecycleStep(2, R.string.step_user_picks, null),
    LifecycleStep(3, R.string.step_receive_uri, "content://…"),
    LifecycleStep(4, R.string.step_show_photo, null),
)

private val PREFS_FILE = TouchedFile(
    path = "permissions/PermissionPrefs.kt",
    role = R.string.role_prefs,
    code = """
        fun wasAsked(permissions: List<String>): Boolean =
            permissions.any { prefs.getBoolean(it, false) }

        fun markAsked(permissions: List<String>) = prefs.edit {
            permissions.forEach { putBoolean(it, true) }
        }
    """.trimIndent(),
)

private val CAMERA_FILES = listOf(
    TouchedFile(
        path = "AndroidManifest.xml",
        role = R.string.role_manifest_camera,
        code = """
            <!-- TODO 1: Declare the CAMERA permission with a <uses-permission> element (keep the uses-feature line below). -->
            <uses-feature android:name="android.hardware.camera" android:required="false" />
        """.trimIndent(),
    ),
    TouchedFile(
        path = "demos/CameraDemo.kt",
        role = R.string.role_camera_demo,
        code = """
            val takePicture = rememberLauncherForActivityResult(
                ActivityResultContracts.TakePicturePreview(),
            ) { bitmap ->
                thumbnail = bitmap
                val result = if (bitmap != null) "miniature ${'$'}{bitmap.width}×${'$'}{bitmap.height}" else "annulé"
                flow.log.step(5, "TakePicturePreview → ${'$'}result")
            }

            PermissionButton(flow, stringResource(R.string.camera_take_photo), case) {
                flow.log.step(5, "CAMERA accordée → l'Intent caméra est autorisé (sinon SecurityException)")
                takePicture.launch(null)
            }
        """.trimIndent(),
    ),
    PREFS_FILE,
)

private val LOCATION_FILES = listOf(
    TouchedFile(
        path = "AndroidManifest.xml",
        role = R.string.role_manifest_location,
        code = """
            <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
            <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
            <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
        """.trimIndent(),
    ),
    TouchedFile(
        path = "demos/LocationDemo.kt",
        role = R.string.role_location_demo,
        code = """
            val backgroundCase = remember(case) {
                case.copy(permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), minSdk = 29)
            }
            val note = if (Build.VERSION.SDK_INT >= 30) "Android 11+ : le système ouvre les Paramètres, pas de boîte de dialogue" else null
            val background = rememberPermissionFlow(backgroundCase, requestNote = note)
            …
            val manager = context.getSystemService(LocationManager::class.java)
            val providers = manager.getProviders(true)
        """.trimIndent(),
    ),
    PREFS_FILE,
)

private val NOTIFICATIONS_FILES = listOf(
    TouchedFile(
        path = "AndroidManifest.xml",
        role = R.string.role_manifest_notifications,
        code = """
            <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
            …
            <application
                android:name=".PermDemoApp"
        """.trimIndent(),
    ),
    TouchedFile(
        path = "demos/NotificationsDemo.kt",
        role = R.string.role_notifications_demo,
        code = """
            PermissionButton(flow, stringResource(R.string.notif_send), case) {
                val sent = NotificationHelper.show(
                    context,
                    TEST_NOTIFICATION_ID,
                    context.getString(R.string.notif_test_title),
                    context.getString(R.string.notif_test_text),
                )
        """.trimIndent(),
    ),
    TouchedFile(
        path = "system/NotificationHelper.kt",
        role = R.string.role_notification_helper,
        code = """
            fun canNotify(context: Context): Boolean =
                NotificationManagerCompat.from(context).areNotificationsEnabled()

            @SuppressLint("MissingPermission")
            fun show(context: Context, id: Int, title: String, text: String): Boolean {
                if (!canNotify(context)) return false
        """.trimIndent(),
    ),
    TouchedFile(
        path = "PermDemoApp.kt",
        role = R.string.role_app,
        code = """
            override fun onCreate() {
                super.onCreate()
                NotificationHelper.createChannel(this)
            }
        """.trimIndent(),
    ),
)

private val PHOTO_PICKER_FILES = listOf(
    TouchedFile(
        path = "demos/PhotoPickerDemo.kt",
        role = R.string.role_photo_demo,
        code = """
            var photo by remember { mutableStateOf<ImageBitmap?>(null) }

            // TODO 6: Register a PickVisualMedia launcher. When a photo comes back, log steps 2 and 3 (with the received URI), set photo = loadThumbnail(context, uri), then log step 4.
            …
                    log.waitFor(2)
                    // TODO 6: Launch the picker for images only (PickVisualMediaRequest with PickVisualMedia.ImageOnly).
        """.trimIndent(),
    ),
    TouchedFile(
        path = "AndroidManifest.xml",
        role = R.string.role_manifest_photos,
        code = """
            <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
            <uses-permission android:name="android.permission.READ_MEDIA_VISUAL_USER_SELECTED" />
            <uses-permission
                android:name="android.permission.READ_EXTERNAL_STORAGE"
                android:maxSdkVersion="32" />
        """.trimIndent(),
    ),
)

private val MIC_FILES = listOf(
    TouchedFile(
        path = "AndroidManifest.xml",
        role = R.string.role_manifest_mic,
        code = """
            <!-- TODO 1: Declare the RECORD_AUDIO permission with a <uses-permission> element. -->
        """.trimIndent(),
    ),
    TouchedFile(
        path = "demos/MicDemo.kt",
        role = R.string.role_mic_demo,
        code = """
            PermissionButton(flow, stringResource(R.string.mic_record), case, enabled = !busy) {
                scope.launch {
                    runCatching { recorder.startRecording() }.onFailure {
                        flow.log.step(5, "MediaRecorder.start() → échec : ${'$'}{it.message}")
                        return@launch
                    }
                    state = MicState.RECORDING
                    flow.log.step(5, "MediaRecorder.start() → l'indicateur vert s'allume en haut de l'écran")
                    delay(3_000)
                    recorder.stopRecording()
        """.trimIndent(),
    ),
    TouchedFile(
        path = "system/AudioRecorder.kt",
        role = R.string.role_audio_recorder,
        code = """
            val file = File(context.cacheDir, "demo_micro.m4a")
            …
            fun startRecording() {
                recorder = newRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(file.absolutePath)
                    prepare()
                    start()
                }
            }
        """.trimIndent(),
    ),
)

private val EXACT_ALARM_FILES = listOf(
    TouchedFile(
        path = "AndroidManifest.xml",
        role = R.string.role_manifest_alarm,
        code = """
            <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
            …
            <receiver
                android:name=".system.AlarmReceiver"
                android:exported="false" />
        """.trimIndent(),
    ),
    TouchedFile(
        path = "demos/ExactAlarmDemo.kt",
        role = R.string.role_alarm_demo,
        code = """
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                if (waitingForSettings) {
                    waitingForSettings = false
                    val granted = canScheduleExactAlarms(context)
                    log.step(4, "Retour des Paramètres → canScheduleExactAlarms() = ${'$'}granted")
                    if (granted) schedule()
                }
            }
            …
            log.waitFor(4)
            // TODO 7: Send the user to the exact alarm settings (Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM with the URI "package:" + context.packageName).
        """.trimIndent(),
    ),
    TouchedFile(
        path = "system/AlarmReceiver.kt",
        role = R.string.role_alarm_receiver,
        code = """
            override fun onReceive(context: Context, intent: Intent) {
                val plannedFor = intent.getStringExtra(EXTRA_PLANNED_FOR).orEmpty()
                val receivedAt = EventLogger.now()
                val log = EventLogs.of("exact_alarm")
                log.step(5, "AlarmReceiver.onReceive → prévue ${'$'}plannedFor, reçue ${'$'}receivedAt")
        """.trimIndent(),
    ),
)

object DemoCatalog {
    val cases = listOf(
        DemoCase(
            id = "camera", title = R.string.case_camera, type = PermissionType.DANGEROUS,
            constants = listOf("CAMERA"), shape = ExpressiveShapes.Clover,
            rationale = R.string.rationale_camera, systemPath = R.string.system_camera,
            permissions = listOf(Manifest.permission.CAMERA),
            files = CAMERA_FILES,
        ),
        DemoCase(
            id = "location", title = R.string.case_location, type = PermissionType.DANGEROUS,
            constants = listOf("ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION"),
            shape = ExpressiveShapes.Cookie,
            rationale = R.string.rationale_location, systemPath = R.string.system_location,
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
            partialStatus = PermissionStatus.APPROXIMATE,
            files = LOCATION_FILES,
        ),
        DemoCase(
            id = "notifications", title = R.string.case_notifications, type = PermissionType.DANGEROUS,
            constants = listOf("POST_NOTIFICATIONS"), shape = ExpressiveShapes.SoftBurst,
            rationale = R.string.rationale_notifications, systemPath = R.string.system_notifications,
            permissions = listOf("android.permission.POST_NOTIFICATIONS"),
            minSdk = 33,
            files = NOTIFICATIONS_FILES,
        ),
        DemoCase(
            id = "photo_picker", title = R.string.case_photo_picker, type = PermissionType.NONE,
            constants = emptyList(), shape = ExpressiveShapes.Flower,
            rationale = R.string.rationale_photos, systemPath = R.string.system_photo_picker,
            permissions = photoPermissions(),
            partialStatus = PermissionStatus.PARTIAL,
            files = PHOTO_PICKER_FILES,
        ),
        DemoCase(
            id = "mic", title = R.string.case_mic, type = PermissionType.DANGEROUS,
            constants = listOf("RECORD_AUDIO"), shape = ExpressiveShapes.Clover,
            rationale = R.string.rationale_mic, systemPath = R.string.system_mic,
            permissions = listOf(Manifest.permission.RECORD_AUDIO),
            files = MIC_FILES,
        ),
        DemoCase(
            id = "exact_alarm", title = R.string.case_exact_alarm, type = PermissionType.SPECIAL,
            constants = listOf("SCHEDULE_EXACT_ALARM"), shape = ExpressiveShapes.Sunny,
            rationale = R.string.rationale_exact_alarm, systemPath = R.string.system_exact_alarm,
            permissions = listOf("android.permission.SCHEDULE_EXACT_ALARM"),
            minSdk = 31,
            files = EXACT_ALARM_FILES,
        ),
    )

    fun byId(id: String): DemoCase = cases.first { it.id == id }
}
