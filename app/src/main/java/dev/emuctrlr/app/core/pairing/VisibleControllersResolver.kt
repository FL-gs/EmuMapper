package dev.emuctrlr.app.core.pairing

import dev.emuctrlr.app.core.domain.controllers.DetectedController
import dev.emuctrlr.app.core.input.ControllerInfo

interface VisibleControllersResolver {
    fun resolve(
        controllers: List<DetectedController>
    ): List<ControllerInfo>
}