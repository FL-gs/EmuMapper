package dev.emumapper.app.core.pairing.write

import dev.emumapper.app.core.input.mapping.MappedController
import dev.emumapper.app.core.settings.WriteMode

interface WritePolicy {
    fun decide(
        writeMode: WriteMode,
        controllers: List<MappedController>,
        currentSnapshot: WriteSnapshot,
        lastWrittenSnapshot: WriteSnapshot?
    ): WriteDecision
}
