package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.data.ini.WriteResult

interface ConfigWriter {
    suspend fun write(
        enabledEmulators: Set<String>,
        controllers: List<MappedController>
    ): WriteResult
}
