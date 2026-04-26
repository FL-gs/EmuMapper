package com.example.pairingapp.core.pairing.write

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.data.ini.IniManager
import com.example.pairingapp.data.ini.WriteResult

class IniConfigWriter : ConfigWriter {

    override suspend fun write(
        enabledEmulators: Set<String>,
        controllers: List<ControllerInfo>
    ): WriteResult {
        return IniManager.writeAll(
            enabledEmulators = enabledEmulators,
            controllers = controllers
        )
    }
}