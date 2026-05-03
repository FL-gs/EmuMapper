package com.example.pairingapp.core.domain.controllers

import android.view.InputDevice
import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.input.isGamepadDevice
import com.example.pairingapp.core.input.toControllerInfo
import com.example.pairingapp.core.input.deduplicationKey

class DefaultControllerScanner : ControllerScanner {

    override fun scan(): List<ControllerInfo> {
        return InputDevice.getDeviceIds()
            .asSequence()
            .mapNotNull(InputDevice::getDevice)
            .filter { it.isGamepadDevice() }
            .map { it.toControllerInfo() }
            .distinctBy { it.deduplicationKey() }
            .toList()
    }
}