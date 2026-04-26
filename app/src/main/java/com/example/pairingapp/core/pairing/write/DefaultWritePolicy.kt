package com.example.pairingapp.core.pairing.write

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.settings.WriteMode

class DefaultWritePolicy : WritePolicy {

    override fun decide(
        writeMode: WriteMode,
        controllers: List<ControllerInfo>,
        currentSnapshot: WriteSnapshot,
        lastWrittenSnapshot: WriteSnapshot?
    ): WriteDecision {
        if (currentSnapshot == lastWrittenSnapshot) {
            return WriteDecision.AlreadyWritten
        }

        if (writeMode == WriteMode.MANUAL) {
            return WriteDecision.ManualMode
        }

        if (controllers.isEmpty()) {
            return WriteDecision.NoControllers
        }

        return WriteDecision.ScheduleAutoWrite(currentSnapshot)
    }
}