package com.example.pairingapp.data.ini.dolphin

import com.example.pairingapp.data.emulators.EmulatorPackages
import com.example.pairingapp.data.ini.StoragePaths
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