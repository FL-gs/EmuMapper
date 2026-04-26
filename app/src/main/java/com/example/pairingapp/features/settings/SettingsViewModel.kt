package com.example.pairingapp.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags
import com.example.pairingapp.data.ini.retroarch.RetroArchPaths
import com.example.pairingapp.data.ini.retroarch.RetroArchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val showRetroArchDialog: Boolean = false,
    val pendingRetroArchPackage: String? = null
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onRetroArchToggleRequested(
        packageName: String,
        enabledEmulators: Set<String>,
        onSetEnabledEmulators: (Set<String>) -> Unit,
    ) {
        viewModelScope.launch {
            val expectedPath = RetroArchPaths.appAutoconfigDir().path
            val configuredPath = RetroArchRepository.readAutoconfigDir()

            AppLogger.d(
                LogTags.INI,
                "retroarch check | configured=${configuredPath ?: "unset"} | expected=$expectedPath | match=${configuredPath == expectedPath}"
            )

            if (configuredPath == expectedPath) {
                onSetEnabledEmulators(enabledEmulators + packageName)
                return@launch
            }

            _uiState.value = SettingsUiState(
                showRetroArchDialog = true,
                pendingRetroArchPackage = packageName
            )
        }
    }

    fun confirmRetroArchSetup(
        enabledEmulators: Set<String>,
        onSetEnabledEmulators: (Set<String>) -> Unit
    ) {
        val packageName = _uiState.value.pendingRetroArchPackage ?: return

        viewModelScope.launch {
            val path = RetroArchPaths.appAutoconfigDir().path

            RetroArchRepository.configureRetroArch(
                autoconfigPath = path,
                controllers = emptyList()
            )

            onSetEnabledEmulators(enabledEmulators + packageName)
            dismissRetroArchDialog()
        }
    }

    fun dismissRetroArchDialog() {
        _uiState.value = SettingsUiState()
    }
}