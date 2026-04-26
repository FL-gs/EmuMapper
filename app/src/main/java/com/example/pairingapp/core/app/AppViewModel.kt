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

    fun setInternalControllers(i1: String?, i2: String?) {
        viewModelScope.launch {
            repo.setInternalControllers(i1, i2)
        }
    }

    fun completeOnboarding(i1: String?, i2: String?) {
        viewModelScope.launch {
            repo.setInternalControllers(i1, i2)
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