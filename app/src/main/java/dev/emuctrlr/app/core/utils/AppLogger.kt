package dev.emuctrlr.app.core.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {


    private const val MAX_LOG_SIZE_BYTES = 10_000_000L // 10 MB

    var enabled = false

    var forceErrorForDebug = false

    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    private var lastTag: String? = null

    sealed class LogFileState {
        data object NotCreated : LogFileState()
        data class Ready(val path: String) : LogFileState()
        data class Error(val message: String) : LogFileState()
    }

    private val _logFileState = MutableStateFlow<LogFileState>(LogFileState.NotCreated)
    val logFileState: StateFlow<LogFileState> = _logFileState

    fun init(context: Context) {
        try {
            val dir = File(context.getExternalFilesDir(null), "logs")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, "app_log.txt")
            logFile = file

            _logFileState.value = if (file.exists()) {
                LogFileState.Ready(file.absolutePath)
            } else {
                LogFileState.NotCreated
            }

            Log.d("LOGGER_PATH", file.absolutePath)
        } catch (e: Exception) {
            _logFileState.value = LogFileState.Error("Failed to init log file")
            Log.e("AppLogger", "Failed to init log file", e)
        }
    }

    fun d(tag: String, message: String) {
        Log.d(tag, message)
        writeToFile("D", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        writeToFile("E", tag, message + (throwable?.message ?: ""))
    }

    private fun writeToFile(level: String, tag: String, message: String) {
        if (!enabled) return

        val file = logFile ?: return

        truncateIfNeeded(file)

        val timestamp = dateFormat.format(Date())

        val needsSpacing = lastTag != tag
        lastTag = tag

        val prefix = "$timestamp | $level | $tag | "

        val formatted = message
            .lines()
            .joinToString(separator = "\n") { line -> prefix + line }

        val finalText = buildString {
            if (needsSpacing) append("\n")
            append(formatted)
            append("\n")
        }

        try {
            val wasMissing = !file.exists()

            file.appendText(finalText)

            if (wasMissing) {
                _logFileState.value = LogFileState.Ready(file.absolutePath)
            }
        } catch (e: Exception) {
            _logFileState.value = LogFileState.Error("Failed to write log file")
            Log.e("AppLogger", "Failed to write log file", e)
        }
    }

    private fun truncateIfNeeded(file: File) {
        if (!file.exists()) return
        if (file.length() < MAX_LOG_SIZE_BYTES) return

        try {
            file.writeText(
                "logs truncated (file exceeded 10 MB at ${dateFormat.format(Date())})\n"
            )
            lastTag = null
        } catch (e: Exception) {
            Log.e("AppLogger", "Failed to truncate log file", e)
        }
    }

    fun clearLogs() {
        val file = logFile ?: return

        try {
            file.writeText("logs cleared\n")
            lastTag = null
            Log.d("AppLogger", "Logs cleared")
        } catch (e: Exception) {
            Log.e("AppLogger", "Failed to clear log file", e)
        }
    }
}