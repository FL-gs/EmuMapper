package dev.emuctrlr.app.core.domain.controllers

import dev.emuctrlr.app.core.input.ControllerInfo

interface ControllerAssignmentService {

    fun resolveVisibleControllers(
        internalController: String?
    ): List<ControllerInfo>
}
