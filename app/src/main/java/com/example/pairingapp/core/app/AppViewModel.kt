package com.example.pairingapp.core.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pairingapp.core.settings.AppLanguage
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.data.settings.AppSettingsRepository
import kotlinx.coroutines.launch

class AppViewModel(
    private val repo: AppSettingsRepository
) : ViewModel() {

    val settings = repo.settings

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch {
            repo.setDarkTheme(value)
        }
    }

    fun setLanguage(value: AppLanguage) {
        viewModelScope.launch {
            repo.setLanguage(value)
        }
    }

    fun setEnabledEmulators(value: Set<String>) {
        viewModelScope.launch {
            repo.setEnabledEmulators(value)
        }
    }

    fun setWriteMode(value: WriteMode) {
        viewModelScope.launch {
            repo.setWriteMode(value)
        }
    }

    fun setInternalController(value: String?) {
        viewModelScope.launch {
            repo.setInternalController(value)
        }
    }

    fun markOnboardingDone() {
        viewModelScope.launch {
            repo.setOnboardingDone(true)
        }
    }

    fun setDebugLogs(value: Boolean) {
        viewModelScope.launch {
            repo.setDebugLogs(value)
        }
    }

    fun clearLogs() {
        AppLogger.clearLogs()
    }
}
