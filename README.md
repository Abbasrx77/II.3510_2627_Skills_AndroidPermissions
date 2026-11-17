# Android permissions in Kotlin apps — Unfinished Project

Hands-on companion to the presentation **“Android permissions in Kotlin apps”** (course II.3510, ISEP).

**PermDemo** is a small Jetpack Compose app that walks through six permission cases (camera, location, notifications, Photo Picker, microphone, exact alarm). For each case, the app shows the permission **lifecycle** live (a stepper and a timestamped log) and the **project files** involved.

The project compiles and runs, but key pieces of the permission flow have been removed. Your job is to complete the **7 TODOs**. The lifecycle stepper, the log (also mirrored in Logcat with the tag `PermDemo`) and the status chips tell you whether each exercise works.

> The app UI is in French. Button and status labels are quoted in French below, with a translation.

## Learning goals

- Declare permissions in the manifest.
- Implement the runtime permission cycle: **check → explain → request → handle**.
- Detect a permanent denial and redirect the user to the system Settings.
- Use the Photo Picker, which needs **no** permission.
- Handle a **special** permission (exact alarms) that is granted in Settings rather than through a dialog.

## Prerequisites

- Android Studio (recent stable version) with the Android SDK installed (compileSdk 37).
- An emulator or device running **Android 14 (API 34) or newer**. The cases behave as described in the presentation from API 34 up.
- Basic Kotlin and Jetpack Compose knowledge.

## Getting started

1. Open the project folder in Android Studio and let Gradle sync.
2. Start an emulator (API 34+) or plug in a device with USB debugging enabled.
3. Run the `app` configuration, or from a terminal:

   ```bash
   ./gradlew installDebug
   ```

4. To follow the log, open Logcat in Android Studio and filter on the tag `PermDemo`.

## Project map

```
app/src/main/
├── AndroidManifest.xml
└── java/fr/isep/permdemo/
    ├── permissions/   PermissionFlow (generic cycle), PermissionStatus, PermissionPrefs, EventLogger, DemoCatalog
    ├── demos/         one file per case (CameraDemo, LocationDemo, …)
    ├── system/        NotificationHelper, AudioRecorder, AlarmReceiver
    └── ui/            theme, components, home and detail screens
```

All dangerous-permission demos rely on the single `PermissionFlow` class. Fixing the generic cycle (TODOs 2 to 5) makes the camera, location, notifications, microphone and Photo Picker comparison demos work at the same time.

## The 7 TODOs

Search the project for `TODO` (Android Studio: *View › Tool Windows › TODO*). Each TODO comment describes what to write.

| # | Where | What to do | Expected result |
|---|-------|------------|-----------------|
| 1 | `app/src/main/AndroidManifest.xml` | Declare the `CAMERA` and `RECORD_AUDIO` permissions. | In the Camera and Micro logs, step 0 reads `déclarée` (declared) instead of `ABSENTE du Manifest` (missing from manifest). The system dialog can now appear. |
| 2 | `permissions/PermissionStatus.kt` — `isGranted()` | **Check**: implement the permission check. | Step 1 logs `PERMISSION_GRANTED` once access is granted. Status chips show `Accordée` (granted) and the feature runs. |
| 3 | `permissions/PermissionFlow.kt` — `shouldExplain()` and `showRationale()` | **Explain**: decide whether to explain, then show the explanation sheet. | After a first refusal, the next tap shows the bottom sheet *« Pourquoi … ? »* (“Why …?”) before asking again. Step 2 logs `true`. |
| 4 | `permissions/PermissionFlow.kt` — `rememberPermissionFlow()` and `onResult()`; `permissions/PermissionStatus.kt` — `isPermanentlyDenied()` | **Request / Handle**: register the permission launcher, handle its result, and detect a permanent denial. | The system dialog appears. Step 4 logs the raw answer and the new status. After two refusals, the status becomes `Refus définitif` (permanently denied) and the button turns into `Ouvrir les Paramètres` (open Settings). |
| 5 | `permissions/PermissionFlow.kt` — `openAppSettings()` | **Permanent denial**: open the app's page in the system Settings. | `Ouvrir les Paramètres` opens the PermDemo app info screen. After you enable the permission there and come back, the status refreshes to `Accordée`. |
| 6 | `demos/PhotoPickerDemo.kt` | **Photo Picker**: register and launch the system photo picker (images only). | `Choisir une photo` (choose a photo) opens the picker **without any permission dialog**. The chosen photo is displayed, and steps 0 to 4 light up. |
| 7 | `permissions/PermissionStatus.kt` — `canScheduleExactAlarms()`; `demos/ExactAlarmDemo.kt` — `openExactAlarmSettings()` | **Exact alarm** (special permission): check whether exact alarms are allowed, and send the user to the matching Settings screen. | `Alarme dans 10 secondes` (alarm in 10 seconds) opens *Alarms & reminders*. After you allow it and come back, step 4 logs `true`, the alarm is scheduled, and a notification arrives about 10 seconds later. |

Suggested order: 1 → 2 → 4 → 3 → 5 → 6 → 7. Without TODOs 2 and 4, no dangerous permission can be granted in the app.

## Test scenarios

Run them on a clean install (see *Reset* below).

1. **Camera, two refusals**: `Prendre une photo` → *Don't allow* → `Prendre une photo` again → the explanation sheet appears → `Continuer` → *Don't allow*. The status is `Refus définitif` and the button shows `Ouvrir les Paramètres`. Enable the camera in Settings and come back: the status is `Accordée`.
2. **Camera, success**: grant the permission. The camera opens and the thumbnail is displayed.
3. **Location**: in the dialog, choose *Approximate*. The status is `Accordée · approximative`. Then `Autoriser en arrière-plan` (allow in background) opens Settings instead of a dialog, and the log says so.
4. **Notifications**: the dialog appears, then the test notification arrives.
5. **Photo Picker**: a photo is displayed with no permission dialog. With `Version avec permission` (version with permission) and *Allow limited access*, the status is `Accès partiel` (partial access).
6. **Microphone**: the green privacy indicator appears at the top of the screen during the 3-second recording, then playback works.
7. **Exact alarm**: denied by default. After you allow it in Settings, the alarm notification arrives 10 seconds later.
8. **Log**: every step appears both in the in-app log and in Logcat (tag `PermDemo`).

## Reset between tests

The app remembers in `SharedPreferences` that a permission was already requested; this is how it tells “never asked” apart from “permanently denied”. The simplest full reset is to reinstall:

```bash
adb uninstall fr.isep.permdemo
./gradlew installDebug
```

To reset a single runtime permission, including after a permanent denial:

```bash
adb shell pm revoke fr.isep.permdemo android.permission.CAMERA
adb shell pm clear-permission-flags fr.isep.permdemo android.permission.CAMERA user-set user-fixed
```

To reset the exact alarm permission:

```bash
adb shell appops set fr.isep.permdemo SCHEDULE_EXACT_ALARM default
```

To clear the app data, including the “already asked” flags:

```bash
adb shell pm clear fr.isep.permdemo
```
