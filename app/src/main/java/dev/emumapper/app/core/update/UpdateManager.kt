package dev.emumapper.app.core.update

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/*
 * Manages the app update system state.
 *
 * Runs the startup check, filters out ignored versions,
 * then exposes the available update to the UI.
 */

class UpdateManager(
    private val appUpdateChecker: AppUpdateChecker,
    private val currentVersionName: String
) {
    private val _availableUpdate = MutableStateFlow<AppUpdateInfo?>(null)
    val availableUpdate: StateFlow<AppUpdateInfo?> = _availableUpdate.asStateFlow()

    private var updateCheckStarted = false

    suspend fun checkForUpdatesOnLaunch(
        ignoredUpdateVersion: String?
    ) {
        if (updateCheckStarted) return
        updateCheckStarted = true

        val update = appUpdateChecker.checkForUpdate(
            currentVersionName = currentVersionName
        )

        _availableUpdate.value = if (
            update != null &&
            update.versionName != ignoredUpdateVersion
        ) {
            update
        } else {
            null
        }
    }

    fun dismissUpdateDialog() {
        _availableUpdate.value = null
    }
}