package com.example.pairingapp.data.settings.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object SettingsKeys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val LANGUAGE = stringPreferencesKey("language")
    val ENABLED_EMULATORS = stringSetPreferencesKey("enabled_emulators")

    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    val INTERNAL_CONTROLLER_1 = stringPreferencesKey("internal_controller_1")
    val INTERNAL_CONTROLLER_2 = stringPreferencesKey("internal_controller_2")

    val WRITE_MODE = stringPreferencesKey("write_mode")

    val DEBUG_LOGS = booleanPreferencesKey("debug_logs")
}
