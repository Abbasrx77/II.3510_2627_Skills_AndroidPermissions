package fr.isep.permdemo.system

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder(private val context: Context) {
    val file = File(context.cacheDir, "demo_micro.m4a")
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

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

    fun stopRecording() {
        recorder?.run {
            runCatching { stop() }
            release()
        }
        recorder = null
    }

    fun play(onFinished: () -> Unit) {
        player?.release()
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener { onFinished() }
            prepare()
            start()
        }
    }

    fun release() {
        stopRecording()
        player?.release()
        player = null
    }

    private fun newRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= 31) MediaRecorder(context)
        else @Suppress("DEPRECATION") MediaRecorder()
}
