package com.example.pairingapp.core.utils

import android.os.Build
import com.example.pairingapp.BuildConfig
import com.example.pairingapp.data.settings.AppSettings

object DebugContextLogger {

    fun logAppInfo() {
        AppLogger.d(
            LogTags.DEBUG,
            """
            ================================
            LOGS ACTIVE
            appId        = ${BuildConfig.APPLICATION_ID}
            versionName  = ${BuildConfig.VERSION_NAME}
            versionCode  = ${BuildConfig.VERSION_CODE}
            ================================
            """.trimIndent()
        )
    }

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

    fun logSettingsChanges(
        previous: AppSettings,
        current: AppSettings,
        hasActiveHosts: Boolean
    ) {
        if (previous.onboardingDone != current.onboardingDone) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | onboardingDone=${previous.onboardingDone} -> ${current.onboardingDone}")
        }

        if (previous.internalController1 != current.internalController1) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | internalController1=${previous.internalController1} -> ${current.internalController1}")
        }

        if (previous.internalController2 != current.internalController2) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | internalController2=${previous.internalController2} -> ${current.internalController2}")
        }

        if (previous.darkTheme != current.darkTheme) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | darkTheme=${previous.darkTheme} -> ${current.darkTheme}")
        }

        if (previous.language != current.language) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | language=${previous.language} -> ${current.language}")
        }

        if (previous.enabledEmulators != current.enabledEmulators) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | enabledEmulators=${previous.enabledEmulators} -> ${current.enabledEmulators}")
        }

        if (previous.writeMode != current.writeMode) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | writeMode=${previous.writeMode} -> ${current.writeMode}")
        }

        if (previous.debugLogs != current.debugLogs) {
            AppLogger.d(LogTags.DATASTORE, "settings changed | debugLogs=${previous.debugLogs} -> ${current.debugLogs}")
        }
    }
}