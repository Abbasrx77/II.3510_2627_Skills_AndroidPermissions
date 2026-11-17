package fr.isep.permdemo.permissions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class LogEvent(val time: String, val step: Int, val message: String)

class EventLogger(private val caseId: String) {
    val events = mutableStateListOf<LogEvent>()

    val passedAt = mutableStateMapOf<Int, String>()

    var inProgress by mutableStateOf<Int?>(null)
        private set

    fun step(number: Int, message: String) {
        val time = now()
        if (number == 1) (2..5).forEach { passedAt.remove(it) }
        passedAt[number] = time
        inProgress = null
        events.add(0, LogEvent(time, number, message))
        Log.i(TAG, "[$caseId] $number · $message")
    }

    fun waitFor(number: Int) {
        inProgress = number
    }

    fun clear() {
        events.clear()
        passedAt.clear()
        inProgress = null
    }

    companion object {
        const val TAG = "PermDemo"
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        fun now(): String = LocalTime.now().format(TIME_FORMAT)
    }
}

object EventLogs {
    private val loggers = mutableMapOf<String, EventLogger>()
    fun of(caseId: String): EventLogger = loggers.getOrPut(caseId) { EventLogger(caseId) }
}
