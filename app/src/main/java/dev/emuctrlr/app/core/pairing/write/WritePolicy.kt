package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.core.settings.WriteMode

interface WritePolicy {
    fun decide(
        writeMode: WriteMode,
        controllers: List<ControllerInfo>,
        currentSnapshot: WriteSnapshot,
        lastWrittenSnapshot: WriteSnapshot?
    ): WriteDecision
}