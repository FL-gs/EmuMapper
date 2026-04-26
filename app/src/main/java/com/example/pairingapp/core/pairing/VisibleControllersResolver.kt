package com.example.pairingapp.core.pairing

import com.example.pairingapp.core.domain.controllers.DetectedController
import com.example.pairingapp.core.input.ControllerInfo

interface VisibleControllersResolver {
    fun resolve(
        controllers: List<DetectedController>
    ): List<ControllerInfo>
}