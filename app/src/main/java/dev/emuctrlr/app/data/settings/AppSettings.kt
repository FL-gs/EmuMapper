package dev.emuctrlr.app.data.settings

import dev.emuctrlr.app.core.input.mapping.ControllerMapping
import dev.emuctrlr.app.core.settings.AppLanguage
import dev.emuctrlr.app.core.settings.WriteMode

data class AppSettings(
    val darkTheme: Boolean = true,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val enabledEmulators: Set<String> = emptySet(),
    val writeMode: WriteMode = WriteMode.MANUAL,
    val onboardingDone: Boolean = false,
    val internalController: String? = null,
    val controllerMappingOverrides: Map<String, ControllerMapping> = emptyMap(),
    val debugLogs: Boolean = false
)
