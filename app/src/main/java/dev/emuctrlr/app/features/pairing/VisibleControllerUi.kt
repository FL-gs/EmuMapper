package dev.emuctrlr.app.features.pairing

import dev.emuctrlr.app.core.input.ControllerInfo

data class VisibleControllerUi(
    val uiKey: String,
    val controller: ControllerInfo
)