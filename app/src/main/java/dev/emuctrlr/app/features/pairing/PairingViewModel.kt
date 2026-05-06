package dev.emuctrlr.app.features.pairing

import androidx.lifecycle.ViewModel
import dev.emuctrlr.app.core.pairing.PairingEngine

class PairingViewModel(
    private val pairingEngine: PairingEngine
) : ViewModel() {
    val lastWriteResult = pairingEngine.lastWriteResult
    val controllers = pairingEngine.visibleControllerUis
    val manualWriteUiState = pairingEngine.manualWriteUiState
    val isCurrentConfigWritten = pairingEngine.isCurrentConfigWritten
    val writeMode = pairingEngine.writeMode

    fun beginManualWriteHold() {
        pairingEngine.beginManualWriteHold()
    }

    fun cancelManualWriteHold() {
        pairingEngine.cancelManualWriteHold()
    }
}
