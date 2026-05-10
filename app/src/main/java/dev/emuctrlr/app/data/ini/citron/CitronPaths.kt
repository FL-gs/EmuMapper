package dev.emuctrlr.app.data.ini.citron

import dev.emuctrlr.app.data.emulators.EmulatorPackages
import dev.emuctrlr.app.data.ini.StoragePaths
import java.io.File

object CitronPaths {

    fun configFile(): File {
        return File(
            StoragePaths.appExternalFilesDir(EmulatorPackages.CITRON),
            "config/config.ini"
        )
    }
}