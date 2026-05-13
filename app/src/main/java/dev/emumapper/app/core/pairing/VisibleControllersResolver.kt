package dev.emumapper.app.core.pairing

import dev.emumapper.app.core.domain.controllers.DetectedController
import dev.emumapper.app.core.input.ControllerInfo

interface VisibleControllersResolver {
    fun resolve(
        controllers: List<DetectedController>
    ): List<ControllerInfo>
}