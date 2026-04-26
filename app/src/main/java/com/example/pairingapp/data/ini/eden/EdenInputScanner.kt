package com.example.pairingapp.data.ini.eden

import android.view.InputDevice
import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.input.isGamepadDevice
import com.example.pairingapp.core.input.toControllerInfo
import com.example.pairingapp.core.input.uniqueKey
import android.util.Log
import com.example.pairingapp.core.utils.AppLogger

data class EdenPortEntry(
    val controller: ControllerInfo,
    val port: Int,
    val guid: String,
    val display: String
)

object EdenInputScanner {

    /**
     * Reproduit le scan Eden/Yuzu :
     *
     * - scan des InputDevice Android
     * - filtre gamepad / joystick
     * - conversion en ControllerInfo
     * - déduplication via uniqueKey()
     * - attribution d'un port (0..n-1)
     * - génération guid + display
     */
    fun scan(): List<EdenPortEntry> {
        val controllers = InputDevice.getDeviceIds()
            .toList()
            .mapNotNull { deviceId -> InputDevice.getDevice(deviceId) }
            .filter { device -> device.isGamepadDevice() }
            .map { device -> device.toControllerInfo() }
            .distinctBy { controller -> controller.uniqueKey() }

        AppLogger.d("MYAPP_EDEN", "scan start")


        return controllers.mapIndexed { index, controller ->
            val guid = "%016x%016x".format(
                controller.productId,
                controller.vendorId
            )

            AppLogger.d(
                "MYAPP_EDEN",
                "port=$index name=${controller.name} " +
                        "vid=${controller.vendorId} pid=${controller.productId} " +
                        "guid=$guid " +
                        "dev=${controller.deviceId} " +
                        "desc=${controller.descriptor?.take(8)} " +
                        "num=${controller.controllerNumber}"
            )

            EdenPortEntry(
                controller = controller,
                port = index,
                guid = guid,
                display = "${controller.name} $index"
            )

        }
    }
}