package dev.emuctrlr.app.data.ini.eden

import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.data.ini.IniLog
import dev.emuctrlr.app.data.ini.WriteResult

object EdenRepository {

    fun writeControllers(
        controllers: List<ControllerInfo>
    ): WriteResult = runCatching {
        val file = EdenPaths.configFile()

        if (!file.exists()) {
            IniLog.fileNotFound("eden_config.ini", file.path)

            return@runCatching WriteResult.Failure(
                emulatorId = "eden",
                reason = "config_file_not_found"
            )
        }

        val original = file.readText()
        val edenDevices = EdenInputScanner.scan()

        val patched = EdenControlsPatcher.patchIni(
            original = original,
            controllers = controllers,
            edenDevices = edenDevices
        )

        if (patched != original) {
            IniLog.verboseDiff("eden_config.ini", original, patched)
            file.writeText(patched)
            IniLog.fileUpdated("eden_config.ini")
        } else {
            IniLog.fileNoChange("eden_config.ini")
        }

        WriteResult.Success
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = "eden",
            reason = throwable.message ?: "write_failed",
            throwable = throwable
        )
    }
}