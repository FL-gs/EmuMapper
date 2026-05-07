package dev.emuctrlr.app.data.ini

import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.core.utils.AppLogger
import dev.emuctrlr.app.core.utils.LogTags

object IniLog {

    private const val VERBOSE = false

    private fun List<MappedController>.toControllerBlock(): String {
        if (isEmpty()) return "  - none"

        return mapIndexed { index, mapped ->
            val controller = mapped.controller
            val mappingHash = mapped.mapping.stableHash().take(12)

            "  - P${index + 1} ${controller.name} | dev=${controller.deviceId} | num=${controller.controllerNumber ?: "-"} | desc=${controller.descriptor?.take(8) ?: "-"} | mapping=${mapped.mappingKey} | mapHash=$mappingHash"
        }.joinToString(separator = "\n")
    }

    private fun Set<String>.toEmulatorBlock(): String {
        if (isEmpty()) return "  - none"

        return toList()
            .sorted()
            .joinToString(separator = "\n") { "  - $it" }
    }

    fun writeRequest(
        enabledEmulators: Set<String>,
        controllers: List<MappedController>
    ) {
        AppLogger.d(
            LogTags.INI,
            "write request\ncontrollers:\n${controllers.toControllerBlock()}\nemulators:\n${enabledEmulators.toEmulatorBlock()}"
        )
    }

    fun skipNoControllers() {
        AppLogger.d(LogTags.INI, "write request | skipped | reason=no_controllers")
    }

    fun emulatorDisabled(emulatorId: String) {
        AppLogger.d(LogTags.INI, "emulator | $emulatorId | skipped | reason=disabled")
    }

    fun emulatorNotImplemented(emulatorId: String) {
        AppLogger.d(LogTags.INI, "emulator | $emulatorId | skipped | reason=not_implemented")
    }

    fun emulatorWriting(emulatorId: String) {
        AppLogger.d(LogTags.INI, "emulator | $emulatorId | start")
    }

    fun fileNotFound(fileName: String, path: String) {
        AppLogger.e(LogTags.INI, "file | $fileName | skipped | reason=not_found | path=$path")
    }

    fun fileNoChange(fileName: String) {
        AppLogger.d(LogTags.INI, "file | $fileName | unchanged")
    }

    fun fileUpdated(fileName: String) {
        AppLogger.d(LogTags.INI, "file | $fileName | updated")
    }

    fun step(message: String) {
        AppLogger.d(LogTags.INI, message)
    }

    fun verboseDiff(fileName: String, original: String, patched: String) {
        if (!VERBOSE) return

        AppLogger.d(LogTags.INI, "----- ORIGINAL $fileName -----\n$original\n-----------------------")
        AppLogger.d(LogTags.INI, "----- PATCHED $fileName -----\n$patched\n-----------------------")
    }
}
