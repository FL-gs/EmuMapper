package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo

data class DetectedController(
    val info: ControllerInfo,
    val type: ControllerType
)