package com.example.pairingapp.core.pairing

import kotlinx.coroutines.flow.StateFlow

interface ManualWriteHandler {

    val progress: StateFlow<Float>
    val showSuccessFlash: StateFlow<Boolean>

    fun start(onCompleted: suspend () -> Unit)
    fun cancel()
}