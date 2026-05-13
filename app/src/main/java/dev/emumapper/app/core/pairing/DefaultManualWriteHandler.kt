package dev.emumapper.app.core.pairing

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MANUAL_WRITE_HOLD_MS = 1_000L
private const val HOLD_PROGRESS_TICK_MS = 16L

class DefaultManualWriteHandler(
    private val scope: CoroutineScope
) : ManualWriteHandler {

    private var job: Job? = null

    private val _uiState = MutableStateFlow<ManualWriteUiState>(ManualWriteUiState.Idle)
    override val uiState: StateFlow<ManualWriteUiState> = _uiState.asStateFlow()

    override fun start(
        onCompleted: suspend () -> Boolean
    ): Boolean {
        if (job != null) return false
        if (_uiState.value !is ManualWriteUiState.Idle) return false

        _uiState.value = ManualWriteUiState.Holding(progress = 0f)

        job = scope.launch {
            val startTime = SystemClock.elapsedRealtime()

            try {
                while (true) {
                    val elapsed = SystemClock.elapsedRealtime() - startTime
                    val progress = (elapsed.toFloat() / MANUAL_WRITE_HOLD_MS.toFloat())
                        .coerceIn(0f, 1f)

                    _uiState.value = ManualWriteUiState.Holding(progress)

                    if (elapsed >= MANUAL_WRITE_HOLD_MS) break
                    delay(HOLD_PROGRESS_TICK_MS)
                }

                _uiState.value = ManualWriteUiState.Writing

                val success = onCompleted()

                _uiState.value = if (success) {
                    ManualWriteUiState.Success
                } else {
                    ManualWriteUiState.Idle
                }
            } finally {
                job = null
            }
        }

        return true
    }

    override fun cancel() {
        if (_uiState.value !is ManualWriteUiState.Holding) return

        job?.cancel()
        job = null
        _uiState.value = ManualWriteUiState.Idle
    }

    override fun resetToIdle() {
        job?.cancel()
        job = null
        _uiState.value = ManualWriteUiState.Idle
    }

    override fun showSuccess() {
        job?.cancel()
        job = null
        _uiState.value = ManualWriteUiState.Success
    }
}