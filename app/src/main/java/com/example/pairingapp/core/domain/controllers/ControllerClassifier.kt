package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo

interface ControllerClassifier {
    fun classify(
        controllers: List<ControllerInfo>,
        internalController1: String?,
        internalController2: String?
    ): List<DetectedController>
}