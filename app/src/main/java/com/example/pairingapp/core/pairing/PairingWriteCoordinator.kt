package com.example.pairingapp.core.pairing

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.input.toLogBlock
import com.example.pairingapp.core.pairing.write.ConfigWriter
import com.example.pairingapp.core.pairing.write.WriteDecision
import com.example.pairingapp.core.pairing.write.WritePolicy
import com.example.pairingapp.core.pairing.write.WriteSnapshot
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags
import com.example.pairingapp.data.ini.WriteResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val AUTO_WRITE_DEBOUNCE_MS = 600L

class PairingWriteCoordinator(
    private val scope: CoroutineScope,
    private val writePolicy: WritePolicy,
    private val configWriter: ConfigWriter
) {
    private var autoWriteJob: Job? = null
    private var lastWrittenSnapshot: WriteSnapshot? = null

    private val manualWriteHandler = DefaultManualWriteHandler(scope)

    private val _isCurrentConfigWritten = MutableStateFlow(false)
    val isCurrentConfigWritten: StateFlow<Boolean> = _isCurrentConfigWritten.asStateFlow()

    private val _lastWriteResult = MutableStateFlow<WriteResult?>(null)
    val lastWriteResult: StateFlow<WriteResult?> = _lastWriteResult.asStateFlow()

    private var manualWriteConsumedUntilRelease = false

    val manualWriteHoldProgress: StateFlow<Float> = manualWriteHandler.progress
    val showSuccessFlash: StateFlow<Boolean> = manualWriteHandler.showSuccessFlash

    fun onStateChanged(
        writeMode: WriteMode,
        controllers: List<ControllerInfo>,
        enabledEmulators: Set<String>
    ) {
        val snapshot = WriteSnapshot(
            controllers = controllers,
            enabledEmulators = enabledEmulators
        )

        updateWrittenState(snapshot)
        cancelPendingAutoWrite(reason = "state_changed")

        when (
            val decision = writePolicy.decide(
                writeMode = writeMode,
                controllers = controllers,
                currentSnapshot = snapshot,
                lastWrittenSnapshot = lastWrittenSnapshot
            )
        ) {
            WriteDecision.AlreadyWritten -> Unit

            WriteDecision.ManualMode -> {
                AppLogger.d(LogTags.INI, "write queue | skipped | reason=manual_mode")
            }

            WriteDecision.NoControllers -> {
                AppLogger.d(LogTags.INI, "write queue | skipped | reason=no_controllers")
            }

            is WriteDecision.ScheduleAutoWrite -> {
                scheduleAutoWrite(decision.snapshot)
            }
        }
    }

    fun beginManualWriteHold(
        writeMode: WriteMode,
        controllers: List<ControllerInfo>,
        enabledEmulators: Set<String>
    ) {
        if (writeMode == WriteMode.AUTO) return
        if (controllers.isEmpty()) return
        if (manualWriteConsumedUntilRelease) return

        val snapshot = WriteSnapshot(
            controllers = controllers,
            enabledEmulators = enabledEmulators
        )

        if (snapshot == lastWrittenSnapshot) {
            AppLogger.d(LogTags.PAIRING, "manual write skipped | already up to date")
            updateWrittenState(snapshot)
            manualWriteConsumedUntilRelease = true
            return
        }

        manualWriteConsumedUntilRelease = true

        manualWriteHandler.start {
            cancelPendingAutoWrite(reason = "manual_write")

            val result = write(snapshot)
            _lastWriteResult.value = result

            when (result) {
                is WriteResult.Success -> {
                    lastWrittenSnapshot = snapshot
                    updateWrittenState(snapshot)
                    AppLogger.d(LogTags.PAIRING, "manual write success | snapshot updated")
                }

                is WriteResult.Failure -> {
                    AppLogger.d(
                        LogTags.PAIRING,
                        "manual write failed | emulator=${result.emulatorId} | reason=${result.reason}"
                    )
                }

                is WriteResult.PartialFailure -> {
                    AppLogger.d(
                        LogTags.PAIRING,
                        "manual write partial failure | fail_count=${result.failures.size} | snapshot NOT updated"
                    )
                }
            }
        }
    }

    fun cancelManualWriteHold() {
        manualWriteConsumedUntilRelease = false
        manualWriteHandler.cancel()
    }

    fun cancelAll() {
        manualWriteConsumedUntilRelease = false
        cancelPendingAutoWrite(reason = "cancel_all")
        manualWriteHandler.cancel()
    }

    private fun updateWrittenState(snapshot: WriteSnapshot) {
        _isCurrentConfigWritten.value = snapshot == lastWrittenSnapshot
    }

    private fun cancelPendingAutoWrite(reason: String) {
        if (autoWriteJob == null) return

        autoWriteJob?.cancel()
        autoWriteJob = null

        AppLogger.d(LogTags.INI, "write queue | canceled | reason=$reason")
    }

    private fun scheduleAutoWrite(snapshot: WriteSnapshot) {
        AppLogger.d(
            LogTags.INI,
            "write queue | scheduled | delay=${AUTO_WRITE_DEBOUNCE_MS}ms\ncontrollers:\n${snapshot.controllers.toLogBlock()}"
        )

        autoWriteJob = scope.launch {
            delay(AUTO_WRITE_DEBOUNCE_MS)

            AppLogger.d(
                LogTags.INI,
                "write queue | firing\ncontrollers:\n${snapshot.controllers.toLogBlock()}"
            )

            val result = write(snapshot)
            _lastWriteResult.value = result

            when (result) {
                is WriteResult.Success -> {
                    lastWrittenSnapshot = snapshot
                    updateWrittenState(snapshot)
                    AppLogger.d(LogTags.PAIRING, "auto write success | snapshot updated")
                }

                is WriteResult.Failure -> {
                    AppLogger.d(
                        LogTags.PAIRING,
                        "auto write failed | emulator=${result.emulatorId} | reason=${result.reason} | snapshot NOT updated"
                    )
                }

                is WriteResult.PartialFailure -> {
                    AppLogger.d(
                        LogTags.PAIRING,
                        "auto write partial failure | fail_count=${result.failures.size} | snapshot NOT updated"
                    )
                }
            }

            autoWriteJob = null
        }
    }

    private suspend fun write(snapshot: WriteSnapshot): WriteResult = withContext(Dispatchers.IO) {
        configWriter.write(
            enabledEmulators = snapshot.enabledEmulators,
            controllers = snapshot.controllers
        )
    }
}