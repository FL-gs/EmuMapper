package dev.emumapper.app.core.domain.controllers

import android.view.InputDevice
import dev.emumapper.app.core.input.ControllerInfo
import dev.emumapper.app.core.input.isGamepadDevice
import dev.emumapper.app.core.input.toControllerInfo
import dev.emumapper.app.core.input.deduplicationKey

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