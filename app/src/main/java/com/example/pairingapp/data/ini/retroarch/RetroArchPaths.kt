package com.example.pairingapp.data.ini.retroarch

import com.example.pairingapp.data.emulators.EmulatorPackages
import com.example.pairingapp.data.ini.StoragePaths
import java.io.File

object RetroArchPaths {

    private val candidatePackages = listOf(
        EmulatorPackages.RETROARCH_32,
        EmulatorPackages.RETROARCH_64
    )
    private fun retroarchBaseDir(): File? {
        return candidatePackages
            .asSequence()
            .map(StoragePaths::appExternalFilesDir)
            .firstOrNull { it.exists() }
    }

    /**
     * Android/data/.../files/retroarch.cfg
     */

    fun retroarchCfg(): File? =
        retroarchBaseDir()?.let { File(it, "retroarch.cfg") }
}