package dev.emumapper.app.core.domain.controllers

import dev.emumapper.app.core.input.ControllerInfo

interface ControllerAssignmentService {

    fun resolveVisibleControllers(
        internalController: String?
    ): List<ControllerInfo>
}
