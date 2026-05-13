package dev.emumapper.app.data.ini.dolphin

import dev.emumapper.app.core.input.mapping.MappedController
import dev.emumapper.app.data.ini.IniLog
import dev.emumapper.app.data.ini.WriteResult

object DolphinRepository {

    private const val EMULATOR_ID = "dolphin"
    private const val DOLPHIN_INI = "Dolphin.ini"
    private const val GCPAD_INI = "GCPadNew.ini"

    fun writeControllers(controllers: List<MappedController>): WriteResult = runCatching {
        val dolphinResult = writeDolphinIni()
        if (dolphinResult is WriteResult.Failure) {
            return@runCatching dolphinResult
        }

        val gcpadResult = writeGcPadIni(controllers)
        if (gcpadResult is WriteResult.Failure) {
            return@runCatching gcpadResult
        }

        WriteResult.Success
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = EMULATOR_ID,
            reason = throwable.message ?: "write_failed",
            throwable = throwable
        )
    }

    private fun writeDolphinIni(): WriteResult {
        val file = DolphinPaths.dolphinIni()

        if (!file.exists()) {
            IniLog.fileNotFound(DOLPHIN_INI, file.path)
            return WriteResult.Failure(
                emulatorId = EMULATOR_ID,
                reason = "config_file_not_found"
            )
        }

        val original = file.readText()
        val patched = DolphinCoreIniPatcher.patchIni(original)

        if (patched != original) {
            IniLog.verboseDiff(DOLPHIN_INI, original, patched)
            file.writeText(patched)
            IniLog.fileUpdated(DOLPHIN_INI)
        } else {
            IniLog.fileNoChange(DOLPHIN_INI)
        }

        return WriteResult.Success
    }

    private fun writeGcPadIni(
        controllers: List<MappedController>
    ): WriteResult {
        val file = DolphinPaths.gcPadIni()

        if (!file.exists()) {
            IniLog.fileNotFound(GCPAD_INI, file.path)
            return WriteResult.Failure(
                emulatorId = EMULATOR_ID,
                reason = "config_file_not_found"
            )
        }

        val original = file.readText()
        val patched = DolphinGcpadIniPatcher.patchIni(
            original = original,
            controllers = controllers
        )

        if (patched != original) {
            IniLog.verboseDiff(GCPAD_INI, original, patched)
            file.writeText(patched)
            IniLog.fileUpdated(GCPAD_INI)
        } else {
            IniLog.fileNoChange(GCPAD_INI)
        }

        return WriteResult.Success
    }
}
