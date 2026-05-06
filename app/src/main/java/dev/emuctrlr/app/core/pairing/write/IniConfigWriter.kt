package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.data.ini.IniManager
import dev.emuctrlr.app.data.ini.WriteResult

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