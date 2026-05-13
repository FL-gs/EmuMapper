package dev.emumapper.app.core.pairing.write

import dev.emumapper.app.core.input.mapping.MappedController
import dev.emumapper.app.data.ini.WriteResult

interface ConfigWriter {
    suspend fun write(
        enabledEmulators: Set<String>,
        controllers: List<MappedController>
    ): WriteResult
}
