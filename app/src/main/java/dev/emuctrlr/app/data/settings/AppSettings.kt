package dev.emuctrlr.app.data.settings

import dev.emuctrlr.app.core.settings.AppLanguage
import dev.emuctrlr.app.core.settings.WriteMode

data class AppSettings(
    val darkTheme: Boolean = false,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val enabledEmulators: Set<String> = emptySet(),

    // Onboarding + manette interne
    val onboardingDone: Boolean = false,
    val internalController: String? = null,

    val writeMode: WriteMode = WriteMode.MANUAL,

    val debugLogs: Boolean = false
)
