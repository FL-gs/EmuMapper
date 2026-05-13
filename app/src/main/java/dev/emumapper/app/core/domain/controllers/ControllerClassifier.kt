package dev.emumapper.app.core.domain.controllers

import dev.emumapper.app.core.input.ControllerInfo

interface ControllerClassifier {
    fun classify(
        controllers: List<ControllerInfo>,
        internalController: String?
    ): List<DetectedController>
}
