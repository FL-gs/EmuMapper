package com.example.pairingapp.core.pairing.write

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.settings.WriteMode

interface WritePolicy {
    fun decide(
        writeMode: WriteMode,
        controllers: List<ControllerInfo>,
        currentSnapshot: WriteSnapshot,
        lastWrittenSnapshot: WriteSnapshot?
    ): WriteDecision
}