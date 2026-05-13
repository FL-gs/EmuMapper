package dev.emumapper.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.emumapper.app.core.input.mapping.ControllerMapping
import dev.emumapper.app.core.input.mapping.ControllerMappingOverridesCodec
import dev.emumapper.app.core.input.mapping.EmuControl
import dev.emumapper.app.core.input.mapping.InputBinding
import dev.emumapper.app.core.input.mapping.MappingProfiles
import dev.emumapper.app.core.input.mapping.toMappingProfileKey
import dev.emumapper.app.core.settings.AppLanguage
import dev.emumapper.app.core.settings.WriteMode
import dev.emumapper.app.data.settings.datastore.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            darkTheme = prefs[SettingsKeys.DARK_THEME] ?: false,
            language = prefs[SettingsKeys.LANGUAGE]
                ?.let { value -> runCatching { AppLanguage.valueOf(value) }.getOrNull() }
                ?: AppLanguage.SYSTEM,
            enabledEmulators = prefs[SettingsKeys.ENABLED_EMULATORS] ?: emptySet(),
            writeMode = prefs[SettingsKeys.WRITE_MODE]
                ?.let { value -> runCatching { WriteMode.valueOf(value) }.getOrNull() }
                ?: WriteMode.MANUAL,
            onboardingDone = prefs[SettingsKeys.ONBOARDING_DONE] ?: false,
            internalController = prefs[SettingsKeys.INTERNAL_CONTROLLER],
            controllerMappingOverrides = ControllerMappingOverridesCodec.decode(
                prefs[SettingsKeys.CONTROLLER_MAPPING_OVERRIDES_JSON]
            ),
            debugLogs = prefs[SettingsKeys.DEBUG_LOGS] ?: false
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.DARK_THEME] = enabled }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[SettingsKeys.LANGUAGE] = language.name }
    }

    suspend fun setEnabledEmulators(enabledPackages: Set<String>) {
        dataStore.edit { it[SettingsKeys.ENABLED_EMULATORS] = enabledPackages }
    }

    suspend fun setWriteMode(mode: WriteMode) {
        dataStore.edit { it[SettingsKeys.WRITE_MODE] = mode.name }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { it[SettingsKeys.ONBOARDING_DONE] = done }
    }

    suspend fun setInternalController(value: String?) {
        dataStore.edit { prefs ->
            if (value == null) {
                prefs.remove(SettingsKeys.INTERNAL_CONTROLLER)
            } else {
                prefs[SettingsKeys.INTERNAL_CONTROLLER] = value
            }
        }
    }

    suspend fun setControllerMappingBinding(
        controllerName: String,
        control: EmuControl,
        binding: InputBinding?
    ) {
        dataStore.edit { prefs ->
            val controllerKey = controllerName.toMappingProfileKey()
            val defaultBinding = MappingProfiles.androidStandard.bindingFor(control)

            val currentOverrides = ControllerMappingOverridesCodec.decode(
                prefs[SettingsKeys.CONTROLLER_MAPPING_OVERRIDES_JSON]
            ).toMutableMap()

            val currentBindings = currentOverrides[controllerKey]
                ?.bindings
                ?.toMutableMap()
                ?: linkedMapOf()

            if (binding == null || binding == defaultBinding) {
                currentBindings.remove(control)
            } else {
                currentBindings[control] = binding
            }

            if (currentBindings.isEmpty()) {
                currentOverrides.remove(controllerKey)
            } else {
                currentOverrides[controllerKey] = ControllerMapping(
                    bindings = currentBindings.toMap()
                )
            }

            writeControllerMappingOverrides(
                prefs = prefs,
                overrides = currentOverrides
            )
        }
    }

    suspend fun resetControllerMapping(controllerName: String) {
        dataStore.edit { prefs ->
            val controllerKey = controllerName.toMappingProfileKey()

            val currentOverrides = ControllerMappingOverridesCodec.decode(
                prefs[SettingsKeys.CONTROLLER_MAPPING_OVERRIDES_JSON]
            ).toMutableMap()

            currentOverrides.remove(controllerKey)

            writeControllerMappingOverrides(
                prefs = prefs,
                overrides = currentOverrides
            )
        }
    }

    suspend fun setDebugLogs(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.DEBUG_LOGS] = enabled }
    }

    private fun writeControllerMappingOverrides(
        prefs: androidx.datastore.preferences.core.MutablePreferences,
        overrides: Map<String, ControllerMapping>
    ) {
        if (overrides.isEmpty()) {
            prefs.remove(SettingsKeys.CONTROLLER_MAPPING_OVERRIDES_JSON)
        } else {
            prefs[SettingsKeys.CONTROLLER_MAPPING_OVERRIDES_JSON] =
                ControllerMappingOverridesCodec.encode(overrides)
        }
    }
}
