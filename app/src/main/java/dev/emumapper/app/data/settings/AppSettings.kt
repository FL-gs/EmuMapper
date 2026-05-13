package dev.emumapper.app.data.settings

import dev.emumapper.app.core.input.mapping.ControllerMapping
import dev.emumapper.app.core.settings.AppLanguage
import dev.emumapper.app.core.settings.WriteMode

data class AppSettings(
    val darkTheme: Boolean = true,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val enabledEmulators: Set<String> = emptySet(),
    val writeMode: WriteMode = WriteMode.MANUAL,
    val onboardingDone: Boolean = false,
    val internalController: String? = null,
    val controllerMappingOverrides: Map<String, ControllerMapping> = emptyMap(),
    val debugLogs: Boolean = false,
    val ignoredUpdateVersion: String? = null
)
