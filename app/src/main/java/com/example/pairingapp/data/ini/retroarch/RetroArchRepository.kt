package com.example.pairingapp.data.ini.retroarch

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.data.ini.IniLog
import com.example.pairingapp.data.ini.WriteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RetroArchRepository {

    fun configureRetroArch(
        autoconfigPath: String,
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

        val autoconfigDir = RetroArchPaths.appAutoconfigDir()
        if (!autoconfigDir.exists()) {
            val created = autoconfigDir.mkdirs()
            IniLog.step("retroarch controllers | autoconfig_dir created=$created | path=${autoconfigDir.path}")

            if (!created && !autoconfigDir.exists()) {
                return WriteResult.Failure(
                    emulatorId = "retroarch",
                    reason = "autoconfig_dir_unavailable"
                )
            }
        }

        val controllerResult = RetroArchControllerRepository.writeControllers(controllers)
        when (controllerResult) {
            is WriteResult.Success -> {
                IniLog.step("retroarch setup | controller_cfgs done")
            }
            is WriteResult.Failure -> return@runCatching controllerResult
            is WriteResult.PartialFailure -> return@runCatching controllerResult
        }

        val original = cfgFile.readText()

        val patchedAutoconfig = RetroArchMainConfigPatcher.patchIni(
            original = original,
            autoconfigPath = autoconfigPath
        )

        val patched = RetroArchReservationPatcher.patchCfg(
            original = patchedAutoconfig,
            controllers = controllers
        )

        if (patched != original) {
            IniLog.step("retroarch setup | joypad_autoconfig_dir=$autoconfigPath")
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

    suspend fun readAutoconfigDir(): String? = withContext(Dispatchers.IO) {
        val cfgFile = RetroArchPaths.retroarchCfg() ?: return@withContext null
        if (!cfgFile.exists()) return@withContext null

        val line = cfgFile.readLines().firstOrNull {
            it.trim().startsWith("joypad_autoconfig_dir")
        } ?: return@withContext null

        return@withContext line.substringAfter("=")
            .trim()
            .removeSurrounding("\"")
    }
}