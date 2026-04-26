package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.pairing.VisibleControllersResolver

class DefaultControllerAssignmentService(
    private val scanner: ControllerScanner,
    private val classifier: ControllerClassifier,
    private val resolver: VisibleControllersResolver
) : ControllerAssignmentService {

    override fun resolveVisibleControllers(
        internalController1: String?,
        internalController2: String?
    ): List<ControllerInfo> {

        val scanned = scanner.scan()

        val classified = classifier.classify(
            controllers = scanned,
            internalController1 = internalController1,
            internalController2 = internalController2
        )

        return resolver.resolve(classified)
    }
}