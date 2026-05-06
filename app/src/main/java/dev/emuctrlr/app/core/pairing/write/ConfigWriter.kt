package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.data.ini.WriteResult

interface ConfigWriter {
    suspend fun write(
        enabledEmulators: Set<String>,
        controllers: List<ControllerInfo>
    ): WriteResult
}