package dev.emumapper.app.core.input.mapping

import dev.emumapper.app.core.input.ControllerInfo

/*
 * Visible controller in the app with its final mapping.
 */
data class MappedController(
    val controller: ControllerInfo,
    val mapping: ControllerMapping
) {
    val mappingKey: String = controller.mappingProfileKey()
}
