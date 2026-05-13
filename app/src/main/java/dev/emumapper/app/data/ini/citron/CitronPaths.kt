package dev.emumapper.app.data.ini.citron

import dev.emumapper.app.data.emulators.EmulatorPackages
import dev.emumapper.app.data.ini.StoragePaths
import java.io.File

object CitronPaths {

    fun configFile(): File {
        return File(
            StoragePaths.appExternalFilesDir(EmulatorPackages.CITRON),
            "config/config.ini"
        )
    }
}