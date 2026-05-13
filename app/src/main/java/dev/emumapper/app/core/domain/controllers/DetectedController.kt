package dev.emumapper.app.core.domain.controllers

import dev.emumapper.app.core.input.ControllerInfo

data class DetectedController(
    val info: ControllerInfo,
    val type: ControllerType
)