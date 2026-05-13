package dev.emumapper.app.core.pairing.write

import dev.emumapper.app.core.input.mapping.MappedController
import dev.emumapper.app.data.ini.IniManager
import dev.emumapper.app.data.ini.WriteResult

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
