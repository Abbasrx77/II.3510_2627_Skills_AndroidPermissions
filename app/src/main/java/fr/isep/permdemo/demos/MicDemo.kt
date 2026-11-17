package fr.isep.permdemo.demos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fr.isep.permdemo.R
import fr.isep.permdemo.permissions.DemoCase
import fr.isep.permdemo.permissions.PermissionFlow
import fr.isep.permdemo.system.AudioRecorder
import fr.isep.permdemo.ui.components.OutlinedPillButton
import fr.isep.permdemo.ui.components.ResultCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MicState { IDLE, RECORDING, READY, PLAYING }

@Composable
fun MicDemo(case: DemoCase, flow: PermissionFlow) {
    val context = LocalContext.current
    val recorder = remember { AudioRecorder(context) }
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(MicState.IDLE) }
    DisposableEffect(Unit) { onDispose { recorder.release() } }

    val busy = state == MicState.RECORDING || state == MicState.PLAYING
    PermissionButton(flow, stringResource(R.string.mic_record), case, enabled = !busy) {
        scope.launch {
            runCatching { recorder.startRecording() }.onFailure {
                flow.log.step(5, "MediaRecorder.start() → échec : ${it.message}")
                return@launch
            }
            state = MicState.RECORDING
            flow.log.step(5, "MediaRecorder.start() → l'indicateur vert s'allume en haut de l'écran")
            delay(3_000)
            recorder.stopRecording()
            state = MicState.READY
            flow.log.step(5, "MediaRecorder.stop() → ${recorder.file.name}, ${recorder.file.length() / 1024} Ko")
        }
    }

    OutlinedPillButton(
        text = stringResource(R.string.mic_play),
        enabled = state == MicState.READY,
        onClick = {
            state = MicState.PLAYING
            flow.log.step(5, "MediaPlayer.start() → lecture")
            recorder.play(onFinished = { state = MicState.READY })
        },
    )

    when (state) {
        MicState.RECORDING -> ResultCard(stringResource(R.string.mic_recording))
        MicState.READY, MicState.PLAYING -> ResultCard(
            stringResource(R.string.mic_ready, recorder.file.name, recorder.file.length() / 1024),
        )
        MicState.IDLE -> Unit
    }
}
