package dev.emuctrlr.app.data.settings

import dev.emuctrlr.app.core.input.mapping.ControllerMapping
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

    /**
     * Overrides utilisateur par nom de manette normalisé.
     *
     * Exemple de clé : "8bitdo ultimate 2c wireless controller".
     * Les valeurs ne stockent que les boutons modifiés par l'utilisateur.
     */
    val controllerMappingOverrides: Map<String, ControllerMapping> = emptyMap(),

    val debugLogs: Boolean = false
)
