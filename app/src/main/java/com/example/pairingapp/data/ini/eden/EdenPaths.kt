package com.example.pairingapp.data.ini.eden

import com.example.pairingapp.data.emulators.EmulatorPackages
import com.example.pairingapp.data.ini.StoragePaths
import java.io.File

object EdenPaths {

    fun configFile(): File {
        return File(
            StoragePaths.appExternalFilesDir(EmulatorPackages.EDEN),
            "config/config.ini"
        )
    }
}