package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.core.settings.WriteMode

interface WritePolicy {
    fun decide(
        writeMode: WriteMode,
        controllers: List<MappedController>,
        currentSnapshot: WriteSnapshot,
        lastWrittenSnapshot: WriteSnapshot?
    ): WriteDecision
}
