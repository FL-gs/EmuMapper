package dev.emumapper.app.core.update

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gère l'état du système de mise à jour.
 *
 * Lance le check au démarrage, filtre si version ignorée,
 * puis expose l'update disponible à l'UI.
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