package dev.emuctrlr.app.core.domain.controllers

import android.view.InputDevice
import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.core.input.isGamepadDevice
import dev.emuctrlr.app.core.input.toControllerInfo
import dev.emuctrlr.app.core.input.deduplicationKey

class DefaultControllerScanner : ControllerScanner {

    override fun scan(): List<ControllerInfo> {
        return InputDevice.getDeviceIds()
            .asSequence()
            .mapNotNull(InputDevice::getDevice)
            .filter { it.isGamepadDevice() }
            .map { it.toControllerInfo() }
            .distinctBy { it.deduplicationKey() }
            .mapIndexed { index, controller ->
                controller.copy(yuzuPort = index)
            }
            .toList()
    }
}