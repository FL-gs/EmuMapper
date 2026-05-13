package dev.emumapper.app

import android.app.Application
import dev.emumapper.app.core.app.AppGraph
import dev.emumapper.app.core.utils.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PairingApplication : Application() {

    lateinit var appGraph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()

        AppLogger.init(this)

        appGraph = AppGraph(this)

        observeDebugLogs()
    }

    private fun observeDebugLogs() {
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            appGraph.settingsRepository.settings
                .map { it.debugLogs }
                .distinctUntilChanged()
                .collect { enabled ->

                    if (enabled) {
                        AppLogger.enabled = true
                        AppLogger.init(this@PairingApplication)

                    } else {
                        AppLogger.enabled = false
                    }
                }
        }
    }
}