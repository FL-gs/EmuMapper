package com.example.pairingapp.core.pairing.write

import com.example.pairingapp.core.input.ControllerInfo

data class WriteSnapshot(
    val controllers: List<ControllerInfo>,
    val enabledEmulators: Set<String>
)