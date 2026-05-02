package com.example.pairingapp.core.pairing

import android.os.SystemClock
import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.input.uniqueKey
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags
import com.example.pairingapp.features.pairing.VisibleControllerUi

private const val PROXY_EVENT_WINDOW_MS = 120L
private const val PROXY_EVENT_RETENTION_MS = 1_000L

class VisibleControllerUiTracker {

    private enum class DeviceEventType {
        ADD,
        REMOVE
    }

    private data class RecentDeviceEvent(
        val type: DeviceEventType,
        val deviceId: Int,
        val timestampMs: Long
    )

    private data class UiAssignedController(
        val info: ControllerInfo,
        val uiKey: String
    )

    private val externalUiKeyByDeviceId = mutableMapOf<Int, String>()
    private val externalUiOrder = mutableListOf<String>()
    private val recentDeviceEvents = ArrayDeque<RecentDeviceEvent>()
    private var nextExternalUiKey = 0L

    fun recordDeviceAdded(deviceId: Int) {
        recordRecentDeviceEvent(
            type = DeviceEventType.ADD,
            deviceId = deviceId
        )
    }

    fun recordDeviceRemoved(deviceId: Int) {
        recordRecentDeviceEvent(
            type = DeviceEventType.REMOVE,
            deviceId = deviceId
        )
    }

    fun clear() {
        externalUiKeyByDeviceId.clear()
        externalUiOrder.clear()
        recentDeviceEvents.clear()
        nextExternalUiKey = 0L
    }

    fun buildVisibleControllerUis(
        controllers: List<ControllerInfo>
    ): List<VisibleControllerUi> {
        if (controllers.isEmpty()) {
            clearExternalUiState()
            return emptyList()
        }

        val externalControllers = controllers.filter { !it.isInternal }
        val internalControllers = controllers.filter { it.isInternal }

        return if (externalControllers.isNotEmpty()) {
            buildExternalVisibleControllerUis(externalControllers)
        } else {
            clearExternalUiState()
            buildInternalVisibleControllerUis(internalControllers)
        }
    }

    private fun clearExternalUiState() {
        externalUiKeyByDeviceId.clear()
        externalUiOrder.clear()
        recentDeviceEvents.clear()
        nextExternalUiKey = 0L
    }

    private fun buildExternalVisibleControllerUis(
        controllers: List<ControllerInfo>
    ): List<VisibleControllerUi> {
        val proxyTransferMap = buildProxyTransferMap()

        val currentUiKeysByDeviceId = mutableMapOf<Int, String>()

        val assignedControllers = controllers.map { info ->
            val transferredUiKey = proxyTransferMap[info.deviceId]
                ?.let { removedDeviceId -> externalUiKeyByDeviceId[removedDeviceId] }

            val uiKey = externalUiKeyByDeviceId[info.deviceId]
                ?: transferredUiKey
                ?: newExternalUiKey()

            if (uiKey !in externalUiOrder) {
                externalUiOrder += uiKey
            }

            AppLogger.d(
                LogTags.INPUT_PROXY,
                "ui assign | dev=${info.deviceId} | name=${info.name} | num=${info.controllerNumber ?: "-"} | desc=${info.descriptor?.take(8) ?: "-"} | uiKey=$uiKey" +
                        (proxyTransferMap[info.deviceId]?.let { " | transferredFrom=$it" } ?: "")
            )

            currentUiKeysByDeviceId[info.deviceId] = uiKey

            UiAssignedController(
                info = info,
                uiKey = uiKey
            )
        }

        externalUiKeyByDeviceId.clear()
        externalUiKeyByDeviceId.putAll(currentUiKeysByDeviceId)

        val orderedControllers = assignedControllers
            .sortedWith(
                compareBy<UiAssignedController> { externalUiOrder.indexOf(it.uiKey) }
                    .thenBy { it.info.controllerNumber ?: Int.MAX_VALUE }
                    .thenBy { it.info.deviceId }
            )

        AppLogger.d(
            LogTags.INPUT_PROXY,
            "ui order | ${
                orderedControllers.mapIndexed { index, assigned ->
                    "P${index + 1}=${assigned.info.name}[dev=${assigned.info.deviceId},ui=${assigned.uiKey},num=${assigned.info.controllerNumber ?: "-"}]"
                }.joinToString(" ; ")
            }"
        )

        return orderedControllers.map { assigned ->
            VisibleControllerUi(
                uiKey = assigned.uiKey,
                controller = assigned.info
            )
        }
    }

    private fun buildInternalVisibleControllerUis(
        controllers: List<ControllerInfo>
    ): List<VisibleControllerUi> {
        if (externalUiKeyByDeviceId.isNotEmpty() || externalUiOrder.isNotEmpty()) {
            externalUiKeyByDeviceId.clear()
            externalUiOrder.clear()
        }

        return controllers.map { info ->
            VisibleControllerUi(
                uiKey = "internal-${info.uniqueKey()}",
                controller = info
            )
        }
    }

    private fun buildProxyTransferMap(): Map<Int, Int> {
        pruneRecentDeviceEvents()

        if (recentDeviceEvents.isEmpty()) return emptyMap()

        val events = recentDeviceEvents.toList()

        AppLogger.d(
            LogTags.INPUT_PROXY,
            "event window | ${
                events.joinToString(" ; ") { event ->
                    "${event.type}@${event.deviceId}(t=${event.timestampMs})"
                }
            }"
        )

        val bursts = mutableListOf<MutableList<RecentDeviceEvent>>()

        events.forEach { event ->
            val currentBurst = bursts.lastOrNull()

            if (currentBurst == null) {
                bursts += mutableListOf(event)
                return@forEach
            }

            val previousEvent = currentBurst.last()
            if (event.timestampMs - previousEvent.timestampMs <= PROXY_EVENT_WINDOW_MS) {
                currentBurst += event
            } else {
                bursts += mutableListOf(event)
            }
        }

        val transferMap = mutableMapOf<Int, Int>()

        bursts.forEach { burst ->
            val addEvents = burst.filter { it.type == DeviceEventType.ADD }
            val lastRemove = burst.lastOrNull { it.type == DeviceEventType.REMOVE } ?: return@forEach

            if (addEvents.size != 1) return@forEach

            transferMap[addEvents.single().deviceId] = lastRemove.deviceId
        }

        if (transferMap.isNotEmpty()) {
            AppLogger.d(LogTags.INPUT_PROXY, "transfer map | $transferMap")
        }

        return transferMap
    }

    private fun recordRecentDeviceEvent(
        type: DeviceEventType,
        deviceId: Int
    ) {
        pruneRecentDeviceEvents()

        recentDeviceEvents.addLast(
            RecentDeviceEvent(
                type = type,
                deviceId = deviceId,
                timestampMs = SystemClock.elapsedRealtime()
            )
        )

        AppLogger.d(
            LogTags.INPUT_PROXY,
            "event recorded | type=$type | dev=$deviceId | recent=${recentDeviceEvents.size}"
        )
    }

    private fun pruneRecentDeviceEvents() {
        val cutoff = SystemClock.elapsedRealtime() - PROXY_EVENT_RETENTION_MS
        while (
            recentDeviceEvents.isNotEmpty() &&
            recentDeviceEvents.first().timestampMs < cutoff
        ) {
            recentDeviceEvents.removeFirst()
        }
    }

    private fun newExternalUiKey(): String {
        nextExternalUiKey += 1
        val uiKey = "ext-ui-$nextExternalUiKey"
        AppLogger.d(LogTags.INPUT_PROXY, "ui key created | $uiKey")
        return uiKey
    }
}