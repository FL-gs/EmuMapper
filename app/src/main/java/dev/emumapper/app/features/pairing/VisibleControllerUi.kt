package dev.emumapper.app.features.pairing

import dev.emumapper.app.core.input.ControllerInfo

data class VisibleControllerUi(
    val uiKey: String,
    val controller: ControllerInfo
)