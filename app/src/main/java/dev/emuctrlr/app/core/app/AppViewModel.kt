package dev.emuctrlr.app.core.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.emuctrlr.app.core.input.mapping.ControllerMapping
import dev.emuctrlr.app.core.input.mapping.EmuControl
import dev.emuctrlr.app.core.input.mapping.InputBinding
import dev.emuctrlr.app.core.settings.AppLanguage
import dev.emuctrlr.app.core.settings.WriteMode
import dev.emuctrlr.app.core.utils.AppLogger
import dev.emuctrlr.app.data.settings.AppSettingsRepository
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

    fun setControllerMappingOverrides(value: Map<String, ControllerMapping>) {
        viewModelScope.launch {
            repo.setControllerMappingOverrides(value)
        }
    }

    fun setControllerMappingOverride(
        controllerName: String,
        mapping: ControllerMapping?
    ) {
        viewModelScope.launch {
            repo.setControllerMappingOverride(
                controllerName = controllerName,
                mapping = mapping
            )
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
