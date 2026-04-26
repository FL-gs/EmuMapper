package com.example.pairingapp.data.ini.retroarch

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.data.ini.IniLog
import com.example.pairingapp.data.ini.WriteResult
import java.io.File

object RetroArchControllerRepository {

    fun writeControllers(controllers: List<ControllerInfo>): WriteResult = runCatching {
        val autoconfigDir = RetroArchPaths.appAutoconfigDir()

        if (!autoconfigDir.exists()) {
            val created = autoconfigDir.mkdirs()
            IniLog.step("retroarch controllers | autoconfig_dir created=$created | path=${autoconfigDir.path}")
        }

        controllers.forEach { controller ->
            writeControllerConfig(
                autoconfigDir = autoconfigDir,
                controller = controller
            )
        }

        WriteResult.Success
    }.getOrElse { throwable ->
        WriteResult.Failure(
            emulatorId = "retroarch",
            reason = throwable.message ?: "controller_cfg_write_failed",
            throwable = throwable
        )
    }

    private fun writeControllerConfig(
        autoconfigDir: File,
        controller: ControllerInfo
    ) {
        val fileName = sanitizeFileName(controller.name) + ".cfg"
        val file = File(autoconfigDir, fileName)

        val original = if (file.exists()) {
            file.readText()
        } else {
            ""
        }

        val patched = RetroArchControllerConfigPatcher.patchCfg(
            original = original,
            controller = controller
        )

        if (patched != original) {
            file.writeText(patched)
            IniLog.fileUpdated(file.name)
        } else {
            IniLog.fileNoChange(file.name)
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name
            .replace("/", "_")
            .replace("\\", "_")
            .replace(":", "_")
            .replace("*", "_")
            .replace("?", "_")
            .replace("\"", "_")
            .replace("<", "_")
            .replace(">", "_")
            .replace("|", "_")
            .trim()
    }
}