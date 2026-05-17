package dev.emumapper.app.data.ini.eden

import dev.emumapper.app.data.emulators.EmulatorPackages
import dev.emumapper.app.data.ini.StoragePaths
import java.io.File

object EdenPaths {

    fun configFiles(): List<Pair<String, File>> {
        return listOf(
            "eden" to File(
                StoragePaths.appExternalFilesDir(EmulatorPackages.EDEN),
                "config/config.ini"
            ),
            "eden_nightly" to File(
                StoragePaths.appExternalFilesDir(EmulatorPackages.EDEN_NIGHTLY),
                "config/config.ini"
            ),
            "eden_legacy" to File(
                StoragePaths.appExternalFilesDir(EmulatorPackages.EDEN_LEGACY),
                "config/config.ini"
            ),
            "eden_legacy_nightly" to File(
                StoragePaths.appExternalFilesDir(EmulatorPackages.EDEN_LEGACY_NIGHTLY),
                "config/config.ini"
            )
        )
    }
}