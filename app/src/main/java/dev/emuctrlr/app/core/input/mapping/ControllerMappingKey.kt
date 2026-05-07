package dev.emuctrlr.app.core.input.mapping

import dev.emuctrlr.app.core.input.ControllerInfo

/**
 * Clé de profil mapping.
 *
 * On utilise seulement le nom de la manette.
 * Le descriptor/vendorId/productId peuvent être faux quand proxy.
 */
fun ControllerInfo.mappingProfileKey(): String {
    return name.toMappingProfileKey()
}

fun String.toMappingProfileKey(): String {
    return trim()
        .lowercase()
        .replace(Regex("\\s+"), " ")
}
