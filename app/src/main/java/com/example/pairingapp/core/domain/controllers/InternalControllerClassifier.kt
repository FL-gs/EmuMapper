package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.input.internalProfileKey

class InternalControllerClassifier : ControllerClassifier {

    override fun classify(
        controllers: List<ControllerInfo>,
        internalController1: String?,
        internalController2: String?
    ): List<DetectedController> {
        return controllers.map { controller ->
            val key = controller.internalProfileKey()

            val type = if (key == internalController1 || key == internalController2) {
                ControllerType.INTERNAL
            } else {
                ControllerType.EXTERNAL
            }

            DetectedController(
                info = controller.copy(isInternal = type == ControllerType.INTERNAL),
                type = type
            )
        }
    }
}