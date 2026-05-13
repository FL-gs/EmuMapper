package dev.emumapper.app.core.domain.controllers

import dev.emumapper.app.core.input.ControllerInfo
import dev.emumapper.app.core.input.internalProfileKey

class InternalControllerClassifier : ControllerClassifier {

    override fun classify(
        controllers: List<ControllerInfo>,
        internalController: String?
    ): List<DetectedController> {
        return controllers.map { controller ->
            val key = controller.internalProfileKey()

            val type = if (key == internalController) {
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
