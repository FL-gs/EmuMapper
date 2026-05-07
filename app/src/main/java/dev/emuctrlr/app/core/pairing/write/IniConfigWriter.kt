package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.data.ini.IniManager
import dev.emuctrlr.app.data.ini.WriteResult

class IniConfigWriter : ConfigWriter {

    override suspend fun write(
        enabledEmulators: Set<String>,
        controllers: List<MappedController>
    ): WriteResult {
        return IniManager.writeAll(
            enabledEmulators = enabledEmulators,
            controllers = controllers
        )
    }
}
