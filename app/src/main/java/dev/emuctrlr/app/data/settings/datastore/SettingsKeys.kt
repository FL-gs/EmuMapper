package dev.emuctrlr.app.data.settings.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object SettingsKeys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val LANGUAGE = stringPreferencesKey("language")
    val ENABLED_EMULATORS = stringSetPreferencesKey("enabled_emulators")

    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")

    val INTERNAL_CONTROLLER = stringPreferencesKey("internal_controller")

    val WRITE_MODE = stringPreferencesKey("write_mode")

    val CONTROLLER_MAPPING_OVERRIDES_JSON = stringPreferencesKey("controller_mapping_overrides_json")

    val DEBUG_LOGS = booleanPreferencesKey("debug_logs")
}
