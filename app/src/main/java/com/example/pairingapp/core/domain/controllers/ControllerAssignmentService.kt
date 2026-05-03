package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo

interface ControllerAssignmentService {

    fun resolveVisibleControllers(
        internalController: String?
    ): List<ControllerInfo>
}
