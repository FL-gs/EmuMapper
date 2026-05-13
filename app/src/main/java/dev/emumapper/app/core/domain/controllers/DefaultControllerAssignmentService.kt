package dev.emumapper.app.core.domain.controllers

import dev.emumapper.app.core.input.ControllerInfo
import dev.emumapper.app.core.pairing.VisibleControllersResolver

class DefaultControllerAssignmentService(
    private val scanner: ControllerScanner,
    private val classifier: ControllerClassifier,
    private val resolver: VisibleControllersResolver
) : ControllerAssignmentService {

    override fun resolveVisibleControllers(
        internalController: String?
    ): List<ControllerInfo> {

        val scanned = scanner.scan()

        val classified = classifier.classify(
            controllers = scanned,
            internalController = internalController
        )

        return resolver.resolve(classified)
    }
}
