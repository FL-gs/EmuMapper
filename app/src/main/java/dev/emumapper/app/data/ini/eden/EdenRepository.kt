package dev.emumapper.app.data.ini.eden

import dev.emumapper.app.core.input.mapping.MappedController
import dev.emumapper.app.data.ini.IniLog
import dev.emumapper.app.data.ini.WriteResult
import dev.emumapper.app.data.ini.yuzulike.YuzuControlsPatcher

object EdenRepository {

    fun writeControllers(
        controllers: List<MappedController>
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

        val patched = YuzuControlsPatcher.patchIni(
            original = original,
            controllers = controllers
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
