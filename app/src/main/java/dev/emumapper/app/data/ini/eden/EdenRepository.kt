package dev.emumapper.app.data.ini.eden

import dev.emumapper.app.core.input.mapping.MappedController
import dev.emumapper.app.data.ini.IniLog
import dev.emumapper.app.data.ini.WriteResult
import dev.emumapper.app.data.ini.yuzulike.YuzuControlsPatcher
import java.io.File

object EdenRepository {

    fun writeControllers(
        controllers: List<MappedController>
    ): WriteResult = runCatching {
        val files = EdenPaths.configFiles()
            .filter { (_, file) -> file.exists() }

        if (files.isEmpty()) {
            EdenPaths.configFiles().forEach { (_, file) ->
                IniLog.fileNotFound("eden_config.ini", file.path)
            }

            return@runCatching WriteResult.Failure(
                emulatorId = "eden",
                reason = "config_file_not_found"
            )
        }

        val failures = mutableListOf<WriteResult.Failure>()

        files.forEach { (name, file) ->
            when (val result = writeFile(name, file, controllers)) {
                is WriteResult.Success -> Unit
                is WriteResult.Failure -> failures += result
                is WriteResult.PartialFailure -> failures += result.failures
            }
        }

        when {
            failures.isEmpty() -> WriteResult.Success

            failures.size == 1 && files.size == 1 -> failures.first()

            else -> WriteResult.PartialFailure(failures)
        }
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = "eden",
            reason = throwable.message ?: "write_failed",
            throwable = throwable
        )
    }

    private fun writeFile(
        name: String,
        file: File,
        controllers: List<MappedController>
    ): WriteResult = runCatching {
        val original = file.readText()

        val patched = YuzuControlsPatcher.patchIni(
            original = original,
            controllers = controllers
        )

        if (patched != original) {
            IniLog.verboseDiff("eden_config.ini", original, patched)
            file.writeText(patched)
            IniLog.fileUpdated("eden_config.ini | $name")
        } else {
            IniLog.fileNoChange("eden_config.ini | $name")
        }

        WriteResult.Success
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = "eden",
            reason = "$name: ${throwable.message ?: "write_failed"}",
            throwable = throwable
        )
    }
}