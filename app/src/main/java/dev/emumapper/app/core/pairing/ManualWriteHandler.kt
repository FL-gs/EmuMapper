package dev.emumapper.app.core.pairing

import kotlinx.coroutines.flow.StateFlow

interface ManualWriteHandler {

    val uiState: StateFlow<ManualWriteUiState>

    fun start(onCompleted: suspend () -> Boolean): Boolean
    fun cancel()
    fun resetToIdle()
    fun showSuccess()
}