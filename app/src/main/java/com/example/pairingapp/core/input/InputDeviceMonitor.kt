package com.example.pairingapp.core.input

import android.content.Context
import android.hardware.input.InputManager
import android.os.Handler
import android.os.Looper
import android.view.InputDevice
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags

class InputDeviceMonitor(
    context: Context,
    private val onAdded: (deviceId: Int) -> Unit = {},
    private val onRemoved: (deviceId: Int) -> Unit = {},
    private val onChanged: (deviceId: Int) -> Unit = {}
) {
    private val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private var registered = false

    private val listener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) {
            val device = InputDevice.getDevice(deviceId)
            if (device != null) {
                AppLogger.d(
                    LogTags.DEVICE,
                    "device event | added\n  - ${device.toDeviceLogLine()}"
                )
            } else {
                AppLogger.d(LogTags.DEVICE, "device event | added | id=$deviceId | details=unavailable")
            }
            onAdded(deviceId)
        }

        override fun onInputDeviceRemoved(deviceId: Int) {
            AppLogger.d(LogTags.DEVICE, "device event | removed | id=$deviceId")
            onRemoved(deviceId)
        }

        override fun onInputDeviceChanged(deviceId: Int) {
            val device = InputDevice.getDevice(deviceId)
            if (device != null) {
                AppLogger.d(
                    LogTags.DEVICE,
                    "device event | changed\n  - ${device.toDeviceLogLine()}"
                )
            } else {
                AppLogger.d(LogTags.DEVICE, "device event | changed | id=$deviceId | details=unavailable")
            }
            onChanged(deviceId)
        }
    }

    fun start() {
        if (registered) return
        inputManager.registerInputDeviceListener(listener, mainHandler)
        registered = true
    }

    fun stop() {
        if (!registered) return
        inputManager.unregisterInputDeviceListener(listener)
        registered = false
    }
}
