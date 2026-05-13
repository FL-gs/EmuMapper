package dev.emumapper.app.core.input.mapping

import dev.emumapper.app.core.input.ControllerInfo

/*
 * Mapping profile key.
 *
 * Only the controller name is used because descriptor/vendorId/productId
 * can be wrong when Android exposes a proxied device.
 */
fun ControllerInfo.mappingProfileKey(): String {
    return name.toMappingProfileKey()
}

fun String.toMappingProfileKey(): String {
    return trim()
        .lowercase()
        .replace(Regex("\\s+"), " ")
}
