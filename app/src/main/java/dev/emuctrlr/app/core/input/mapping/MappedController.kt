package dev.emuctrlr.app.core.input.mapping

import dev.emuctrlr.app.core.input.ControllerInfo

/*
 * Manettes visible dans l'app + mapping final.
 */
data class MappedController(
    val controller: ControllerInfo,
    val mapping: ControllerMapping
) {
    val mappingKey: String = controller.mappingProfileKey()
}
