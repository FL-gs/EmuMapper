package dev.emumapper.app.core.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.emumapper.app.core.input.mapping.EmuControl
import dev.emumapper.app.core.input.mapping.InputBinding
import dev.emumapper.app.core.settings.AppLanguage
import dev.emumapper.app.core.settings.WriteMode
import dev.emumapper.app.core.update.UpdateManager
import dev.emumapper.app.core.utils.AppLogger
import dev.emumapper.app.data.settings.AppSettingsRepository
import kotlinx.coroutines.launch

class AppViewModel(
    private val repo: AppSettingsRepository,
    private val updateManager: UpdateManager
) : ViewModel() {

    val settings = repo.settings

    val availableUpdate = updateManager.availableUpdate

    fun checkForUpdatesOnLaunch(
        ignoredUpdateVersion: String?
    ) {
        viewModelScope.launch {
            updateManager.checkForUpdatesOnLaunch(
                ignoredUpdateVersion = ignoredUpdateVersion
            )
        }
    }

    fun dismissUpdateDialog() {
        updateManager.dismissUpdateDialog()
    }

    fun ignoreUpdateVersion(versionName: String) {
        viewModelScope.launch {
            repo.setIgnoredUpdateVersion(versionName)
            updateManager.dismissUpdateDialog()
        }
    }

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

    fun setControllerMappingBinding(
        controllerName: String,
        control: EmuControl,
        binding: InputBinding?
    ) {
        viewModelScope.launch {
            repo.setControllerMappingBinding(
                controllerName = controllerName,
                control = control,
                binding = binding
            )
        }
    }

    fun resetControllerMapping(controllerName: String) {
        viewModelScope.launch {
            repo.resetControllerMapping(controllerName)
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