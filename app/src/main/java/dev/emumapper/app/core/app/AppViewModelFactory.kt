package dev.emumapper.app.core.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.emumapper.app.core.update.UpdateManager
import dev.emumapper.app.data.settings.AppSettingsRepository

class AppViewModelFactory(
    private val repo: AppSettingsRepository,
    private val updateManager: UpdateManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(
            repo = repo,
            updateManager = updateManager
        ) as T
    }
}