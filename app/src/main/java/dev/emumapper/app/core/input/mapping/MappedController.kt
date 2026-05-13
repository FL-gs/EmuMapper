package dev.emumapper.app.core.input.mapping

import dev.emumapper.app.core.input.ControllerInfo

/*
 * Manettes visible dans l'app + mapping final.
 */
data class MappedController(
    val controller: ControllerInfo,
    val mapping: ControllerMapping
) {
    val mappingKey: String = controller.mappingProfileKey()
}
