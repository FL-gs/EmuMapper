package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.ControllerInfo

data class WriteSnapshot(
    val controllers: List<ControllerInfo>,
    val enabledEmulators: Set<String>
)