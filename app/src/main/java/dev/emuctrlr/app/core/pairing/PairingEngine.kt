package dev.emuctrlr.app.core.pairing

import android.content.Context
import dev.emuctrlr.app.core.domain.controllers.ControllerAssignmentService
import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.core.input.InputDeviceMonitor
import dev.emuctrlr.app.core.input.mapping.ControllerMappingResolver
import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.core.input.toLogBlock
import dev.emuctrlr.app.core.pairing.write.ConfigWriter
import dev.emuctrlr.app.core.pairing.write.WritePolicy
import dev.emuctrlr.app.core.settings.WriteMode
import dev.emuctrlr.app.core.utils.AppLogger
import dev.emuctrlr.app.core.utils.DebugContextLogger
import dev.emuctrlr.app.core.utils.LogTags
import dev.emuctrlr.app.data.ini.WriteResult
import dev.emuctrlr.app.data.settings.AppSettings
import dev.emuctrlr.app.data.settings.AppSettingsRepository
import dev.emuctrlr.app.features.pairing.VisibleControllerUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val DEVICE_RESYNC_DEBOUNCE_MS = 200L

class PairingEngine(
    context: Context,
    private val settingsRepository: AppSettingsRepository,
    private val controllerAssignmentService: ControllerAssignmentService,
    private val controllerMappingResolver: ControllerMappingResolver,
    writePolicy: WritePolicy,
    configWriter: ConfigWriter
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val appContext = context.applicationContext

    private val writeCoordinator = PairingWriteCoordinator(
        scope = scope,
        writePolicy = writePolicy,
        configWriter = configWriter
    )

    private val inputDeviceMonitor = InputDeviceMonitor(
        context = appContext,
        onAdded = { deviceId ->
            visibleControllerUiTracker.recordDeviceAdded(deviceId)
            scheduleResync("device_added")
        },
        onRemoved = { deviceId ->
            visibleControllerUiTracker.recordDeviceRemoved(deviceId)
            scheduleResync("device_removed")
        },
        onChanged = { scheduleResync("device_changed") }
    )

    private val activeHosts = linkedSetOf<String>()

    private var currentSettings = AppSettings()
    private var settingsInitialized = false
    private var resyncJob: Job? = null

    private val _visibleControllers = MutableStateFlow<List<ControllerInfo>>(emptyList())
    val visibleControllers: StateFlow<List<ControllerInfo>> = _visibleControllers.asStateFlow()
    private val _visibleControllerUis = MutableStateFlow<List<VisibleControllerUi>>(emptyList())
    val visibleControllerUis: StateFlow<List<VisibleControllerUi>> = _visibleControllerUis.asStateFlow()
    val isCurrentConfigWritten: StateFlow<Boolean> = writeCoordinator.isCurrentConfigWritten
    val lastWriteResult: StateFlow<WriteResult?> = writeCoordinator.lastWriteResult

    private val visibleControllerUiTracker = VisibleControllerUiTracker()

    private val _writeMode = MutableStateFlow(WriteMode.MANUAL)
    val writeMode: StateFlow<WriteMode> = _writeMode.asStateFlow()

    val manualWriteUiState: StateFlow<ManualWriteUiState> =
        writeCoordinator.manualWriteUiState

    init {
        scope.launch {
            settingsRepository.settings.collect(::applySettings)
        }
    }

    fun attachHost(hostId: String) {
        scope.launch {
            val added = activeHosts.add(hostId)
            if (!added) return@launch

            AppLogger.d(
                LogTags.PAIRING,
                "pairing host attached | host=$hostId | remaining=${activeHosts.size}"
            )

            if (activeHosts.size == 1) {
                startMonitoring()
            }
        }
    }

    fun detachHost(hostId: String) {
        scope.launch {
            val removed = activeHosts.remove(hostId)
            if (!removed) return@launch

            AppLogger.d(
                LogTags.PAIRING,
                "pairing host detached | host=$hostId | remaining=${activeHosts.size}"
            )

            if (activeHosts.isEmpty()) {
                stopMonitoring()
            }
        }
    }

    fun beginManualWriteHold() {
        writeCoordinator.beginManualWriteHold(
            writeMode = currentSettings.writeMode,
            controllers = resolveMappedControllers(_visibleControllers.value),
            enabledEmulators = currentSettings.enabledEmulators
        )
    }

    fun cancelManualWriteHold() {
        writeCoordinator.cancelManualWriteHold()
    }

    private fun applySettings(settings: AppSettings) {
        val previousSettings = currentSettings
        val wasInitialized = settingsInitialized

        val internalControllerChanged =
            previousSettings.internalController != settings.internalController

        val writeRelevantSettingsChanged =
            previousSettings.writeMode != settings.writeMode ||
                    previousSettings.enabledEmulators != settings.enabledEmulators ||
                    previousSettings.controllerMappingOverrides != settings.controllerMappingOverrides

        val debugLogsJustEnabled =
            !previousSettings.debugLogs && settings.debugLogs

        currentSettings = settings
        settingsInitialized = true
        _writeMode.value = settings.writeMode

        AppLogger.enabled = settings.debugLogs

        if (debugLogsJustEnabled) {
            DebugContextLogger.logAppInfo()
            DebugContextLogger.logDeviceInfo()
        }

        if (!wasInitialized || debugLogsJustEnabled) {
            DebugContextLogger.logAppSettings(
                settings = settings,
                hasActiveHosts = hasActiveHosts()
            )
        } else {
            DebugContextLogger.logSettingsChanges(
                previous = previousSettings,
                current = settings,
                hasActiveHosts = hasActiveHosts()
            )
        }

        if (!hasActiveHosts()) {
            return
        }

        if (!wasInitialized) {
            resyncControllers()
            return
        }

        if (internalControllerChanged) {
            scheduleResync("internal_controller_settings_changed")
            return
        }

        if (writeRelevantSettingsChanged) {
            handleControllersChanged(_visibleControllers.value)
        }
    }

    private fun startMonitoring() {
        inputDeviceMonitor.start()

        resyncJob?.cancel()
        resyncJob = null

        if (settingsInitialized) {
            resyncControllers()
        }
    }

    private fun stopMonitoring() {
        inputDeviceMonitor.stop()

        resyncJob?.cancel()
        resyncJob = null

        visibleControllerUiTracker.clear()
        _visibleControllerUis.value = emptyList()

        writeCoordinator.cancelAll()
    }

    private fun hasActiveHosts(): Boolean {
        return activeHosts.isNotEmpty()
    }

    private fun handleControllersChanged(controllers: List<ControllerInfo>) {
        if (!settingsInitialized) return

        writeCoordinator.onStateChanged(
            writeMode = currentSettings.writeMode,
            controllers = resolveMappedControllers(controllers),
            enabledEmulators = currentSettings.enabledEmulators
        )
    }

    private fun resolveMappedControllers(
        controllers: List<ControllerInfo>
    ): List<MappedController> {
        return controllerMappingResolver.resolveAll(
            controllers = controllers,
            userOverridesByName = currentSettings.controllerMappingOverrides
        )
    }

    private fun scheduleResync(reason: String) {
        if (!hasActiveHosts()) return
        if (!settingsInitialized) return

        resyncJob?.cancel()

        resyncJob = scope.launch {
            delay(DEVICE_RESYNC_DEBOUNCE_MS)
            AppLogger.d(LogTags.PAIRING, "resync firing | reason=$reason")
            resyncControllers()
            resyncJob = null
        }
    }

    private fun resyncControllers() {
        if (!settingsInitialized) return

        val resolvedControllers =
            controllerAssignmentService.resolveVisibleControllers(
                internalController = currentSettings.internalController
            )

        publishResolvedControllers(resolvedControllers)
    }

    private fun publishResolvedControllers(controllers: List<ControllerInfo>) {
        writeCoordinator.cancelManualWriteHold()

        _visibleControllers.value = controllers
        _visibleControllerUis.value =
            visibleControllerUiTracker.buildVisibleControllerUis(controllers)

        val source = if (controllers.any { !it.isInternal }) {
            "external_priority"
        } else {
            "internal_fallback"
        }

        AppLogger.d(
            LogTags.PAIRING,
            "pairing state | active updated | source=$source\ncontrollers:\n${controllers.toLogBlock()}"
        )

        handleControllersChanged(controllers)
    }
}
