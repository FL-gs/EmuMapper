package dev.emuctrlr.app.core.domain.controllers

import dev.emuctrlr.app.core.input.ControllerInfo

data class DetectedController(
    val info: ControllerInfo,
    val type: ControllerType
)