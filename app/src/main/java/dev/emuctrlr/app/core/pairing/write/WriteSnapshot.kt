package dev.emuctrlr.app.core.pairing.write

import dev.emuctrlr.app.core.input.mapping.MappedController

data class WriteSnapshot(
    val controllers: List<MappedController>,
    val enabledEmulators: Set<String>,
    val mappingHash: String = controllers.stableMappingHash()
)

private fun List<MappedController>.stableMappingHash(): String {
    return joinToString(separator = "|") { mapped ->
        "${mapped.mappingKey}:${mapped.mapping.stableHash()}"
    }
}
