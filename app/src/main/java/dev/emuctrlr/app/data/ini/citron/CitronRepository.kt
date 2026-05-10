package dev.emuctrlr.app.data.ini.citron

import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.data.ini.IniLog
import dev.emuctrlr.app.data.ini.WriteResult
import dev.emuctrlr.app.data.ini.yuzulike.YuzuControlsPatcher

object CitronRepository {

    fun writeControllers(
        controllers: List<MappedController>
    ): WriteResult = runCatching {
        val file = CitronPaths.configFile()

        if (!file.exists()) {
            IniLog.fileNotFound("citron_config.ini", file.path)

            return@runCatching WriteResult.Failure(
                emulatorId = "citron",
                reason = "config_file_not_found"
            )
        }

        val original = file.readText()

        val patched = YuzuControlsPatcher.patchIni(
            original = original,
            controllers = controllers
        )

        if (patched != original) {
            IniLog.verboseDiff("citron_config.ini", original, patched)
            file.writeText(patched)
            IniLog.fileUpdated("citron_config.ini")
        } else {
            IniLog.fileNoChange("citron_config.ini")
        }

        WriteResult.Success
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = "citron",
            reason = throwable.message ?: "write_failed",
            throwable = throwable
        )
    }
}