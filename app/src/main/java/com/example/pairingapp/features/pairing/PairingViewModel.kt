package com.example.pairingapp.features.pairing

import androidx.lifecycle.ViewModel
import com.example.pairingapp.core.pairing.PairingEngine

class PairingViewModel(
    private val pairingEngine: PairingEngine
) : ViewModel() {
    val lastWriteResult = pairingEngine.lastWriteResult
    val controllers = pairingEngine.visibleControllerUis
    val manualWriteHoldProgress = pairingEngine.manualWriteHoldProgress
    val isCurrentConfigWritten = pairingEngine.isCurrentConfigWritten
    val showSuccessFlash = pairingEngine.showSuccessFlash
    val writeMode = pairingEngine.writeMode

    fun beginManualWriteHold() {
        pairingEngine.beginManualWriteHold()
    }

    fun cancelManualWriteHold() {
        pairingEngine.cancelManualWriteHold()
    }
}
