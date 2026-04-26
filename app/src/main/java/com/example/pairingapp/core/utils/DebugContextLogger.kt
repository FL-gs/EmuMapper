package com.example.pairingapp.core.utils

import android.os.Build
import com.example.pairingapp.data.settings.AppSettings

object DebugContextLogger {

    fun logDeviceInfo() {
        AppLogger.d(
            LogTags.DEVICE,
            "manufacturer=${Build.MANUFACTURER} | model=${Build.MODEL} | sdk=${Build.VERSION.SDK_INT}"
        )
    }

    fun logAppSettings(
        settings: AppSettings,
        hasActiveHosts: Boolean
    ) {
        AppLogger.d(
            LogTags.DATASTORE,
            """
            settings applied in pairing engine
            onboardingDone       = ${settings.onboardingDone}
            internalController1  = ${settings.internalController1}
            internalController2  = ${settings.internalController2}
            darkTheme            = ${settings.darkTheme}
            language             = ${settings.language}
            enabledEmulators     = ${settings.enabledEmulators}
            writeMode            = ${settings.writeMode}
            debugLogs            = ${settings.debugLogs}
            hasActiveHosts       = $hasActiveHosts
            """.trimIndent()
        )
    }
}