package dev.emuctrlr.app.core.domain.controllers

import dev.emuctrlr.app.core.input.ControllerInfo

interface ControllerClassifier {
    fun classify(
        controllers: List<ControllerInfo>,
        internalController: String?
    ): List<DetectedController>
}
