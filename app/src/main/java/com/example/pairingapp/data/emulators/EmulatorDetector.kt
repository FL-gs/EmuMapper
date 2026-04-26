package com.example.pairingapp.data.emulators

import android.content.Context
import android.content.pm.PackageManager

class EmulatorDetector(
    private val context: Context
) {
    private val pm: PackageManager = context.packageManager

    fun isInstalled(packageName: String): Boolean {
        return try {
            pm.getApplicationInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun installedEmulators(): List<EmulatorDef> {
        return EmulatorCatalog.all.filter { emulator ->
            emulator.packageNames.any { packageName ->
                isInstalled(packageName)
            }
        }
    }
}
