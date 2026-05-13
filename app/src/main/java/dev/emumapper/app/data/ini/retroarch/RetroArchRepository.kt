package dev.emumapper.app.data.ini.retroarch

import dev.emumapper.app.core.input.ControllerInfo
import dev.emumapper.app.data.ini.IniLog
import dev.emumapper.app.data.ini.WriteResult

object RetroArchRepository {

    fun configureRetroArch(
        controllers: List<ControllerInfo>
    ): WriteResult = runCatching {
        val cfgFile = RetroArchPaths.retroarchCfg()

        IniLog.step("retroarch setup | cfg=${cfgFile?.path ?: "null"}")

        if (cfgFile == null || !cfgFile.exists()) {
            IniLog.fileNotFound("retroarch.cfg", cfgFile?.path ?: "null")
            return@runCatching WriteResult.Failure(
                emulatorId = "retroarch",
                reason = "config_file_not_found"
            )
        }

        val original = cfgFile.readText()

        val patched = RetroArchReservationPatcher.patchCfg(
            original = original,
            controllers = controllers
        )

        if (patched != original) {
            cfgFile.writeText(patched)
            IniLog.step("retroarch setup | reservations updated")
        } else {
            IniLog.step("retroarch setup | unchanged")
        }

        WriteResult.Success
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = "retroarch",
            reason = throwable.message ?: "write_failed",
            throwable = throwable
        )
    }
}