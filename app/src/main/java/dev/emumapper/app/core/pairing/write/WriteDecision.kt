package dev.emumapper.app.core.pairing.write

sealed interface WriteDecision {
    data object AlreadyWritten : WriteDecision
    data object ManualMode : WriteDecision
    data object NoControllers : WriteDecision
    data class ScheduleAutoWrite(
        val snapshot: WriteSnapshot
    ) : WriteDecision
}