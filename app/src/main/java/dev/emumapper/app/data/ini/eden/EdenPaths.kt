package dev.emumapper.app.data.ini.eden

import dev.emumapper.app.data.emulators.EmulatorPackages
import dev.emumapper.app.data.ini.StoragePaths
import java.io.File

object EdenPaths {

    fun configFile(): File {
        return File(
            StoragePaths.appExternalFilesDir(EmulatorPackages.EDEN),
            "config/config.ini"
        )
    }
}