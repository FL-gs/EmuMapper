package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo

interface ControllerAssignmentService {

    fun resolveVisibleControllers(
        internalController1: String?,
        internalController2: String?
    ): List<ControllerInfo>
}