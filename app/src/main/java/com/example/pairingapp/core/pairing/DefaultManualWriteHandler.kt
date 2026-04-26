package com.example.pairingapp.core.pairing

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MANUAL_WRITE_HOLD_MS = 600L
private const val HOLD_PROGRESS_TICK_MS = 16L
private const val SUCCESS_FLASH_MS = 800L

class DefaultManualWriteHandler(
    private val scope: CoroutineScope
) : ManualWriteHandler {

    private var job: Job? = null
    private var latched = false

    private val _progress = MutableStateFlow(0f)
    override val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _showSuccessFlash = MutableStateFlow(false)
    override val showSuccessFlash: StateFlow<Boolean> = _showSuccessFlash.asStateFlow()

    override fun start(onCompleted: suspend () -> Unit) {
        if (latched || job != null || _showSuccessFlash.value) return

        _progress.value = 0f

        job = scope.launch {
            val startTime = SystemClock.elapsedRealtime()

            try {
                while (true) {
                    val elapsed = SystemClock.elapsedRealtime() - startTime
                    val progress = (elapsed.toFloat() / MANUAL_WRITE_HOLD_MS.toFloat())
                        .coerceIn(0f, 1f)

                    _progress.value = progress

                    if (elapsed >= MANUAL_WRITE_HOLD_MS) break
                    delay(HOLD_PROGRESS_TICK_MS)
                }

                latched = true

                onCompleted()

                _progress.value = 1f
                _showSuccessFlash.value = true
                delay(SUCCESS_FLASH_MS)
            } finally {
                _showSuccessFlash.value = false
                _progress.value = 0f
                latched = false
                job = null
            }
        }
    }

    override fun cancel() {
        if (_showSuccessFlash.value) return
        if (latched) return

        job?.cancel()
        job = null
        _progress.value = 0f
    }
}