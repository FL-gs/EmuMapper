package dev.emuctrlr.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.emuctrlr.app.core.settings.AppLanguage
import dev.emuctrlr.app.core.settings.WriteMode
import dev.emuctrlr.app.data.settings.datastore.SettingsKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        val lang = runCatching {
            AppLanguage.valueOf(prefs[SettingsKeys.LANGUAGE] ?: AppLanguage.SYSTEM.name)
        }.getOrElse { AppLanguage.SYSTEM }

        val writeMode = runCatching {
            WriteMode.valueOf(
                prefs[SettingsKeys.WRITE_MODE] ?: WriteMode.MANUAL.name
            )
        }.getOrElse { WriteMode.MANUAL }

        AppSettings(
            darkTheme = prefs[SettingsKeys.DARK_THEME] ?: false,
            language = lang,
            enabledEmulators = prefs[SettingsKeys.ENABLED_EMULATORS] ?: emptySet(),
            writeMode = writeMode,
            onboardingDone = prefs[SettingsKeys.ONBOARDING_DONE] ?: false,
            internalController = prefs[SettingsKeys.INTERNAL_CONTROLLER],
            debugLogs = prefs[SettingsKeys.DEBUG_LOGS] ?: false,
        )
    }

    suspend fun setDarkTheme(value: Boolean) {
        dataStore.edit { it[SettingsKeys.DARK_THEME] = value }
    }

    suspend fun setLanguage(value: AppLanguage) {
        dataStore.edit { it[SettingsKeys.LANGUAGE] = value.name }
    }

    suspend fun setEnabledEmulators(value: Set<String>) {
        dataStore.edit { it[SettingsKeys.ENABLED_EMULATORS] = value }
    }

    suspend fun setWriteMode(value: WriteMode) {
        dataStore.edit { it[SettingsKeys.WRITE_MODE] = value.name }
    }

    suspend fun setOnboardingDone(value: Boolean) {
        dataStore.edit { it[SettingsKeys.ONBOARDING_DONE] = value }
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

    suspend fun setDebugLogs(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.DEBUG_LOGS] = enabled }
    }
}
