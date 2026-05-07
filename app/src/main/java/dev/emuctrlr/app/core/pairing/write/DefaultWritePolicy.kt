package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.core.settings.WriteMode

class DefaultWritePolicy : WritePolicy {

    override fun decide(
        writeMode: WriteMode,
        controllers: List<MappedController>,
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
