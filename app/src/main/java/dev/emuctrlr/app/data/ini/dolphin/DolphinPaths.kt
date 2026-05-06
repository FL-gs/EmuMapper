package dev.emuctrlr.app.data.ini.dolphin

import dev.emuctrlr.app.data.emulators.EmulatorPackages
import dev.emuctrlr.app.data.ini.StoragePaths
import java.io.File

object DolphinPaths {

    private fun configDir(): File {
        return File(
            StoragePaths.appExternalFilesDir(EmulatorPackages.DOLPHIN),
            "Config"
        )
    }

    fun gcPadIni(): File = File(configDir(), "GCPadNew.ini")
    fun dolphinIni(): File = File(configDir(), "Dolphin.ini")
}