package com.example.pairingapp.core.pairing.write

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.data.ini.WriteResult

interface ConfigWriter {
    suspend fun write(
        enabledEmulators: Set<String>,
        controllers: List<ControllerInfo>
    ): WriteResult
}