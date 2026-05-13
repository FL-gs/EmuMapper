package dev.emumapper.app.data.settings.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.settingsDataStore by preferencesDataStore(name = "app_settings")
