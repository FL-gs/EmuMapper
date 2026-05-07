package dev.emuctrlr.app.core.input.mapping

import dev.emuctrlr.app.core.input.ControllerInfo

/**
 * Contrôleur visible + mapping résolu.
 *
 * Cette classe servira plus tard à remplacer List<ControllerInfo> dans le pipeline d'écriture.
 */
data class MappedController(
    val controller: ControllerInfo,
    val mapping: ControllerMapping
) {
    val mappingKey: String = controller.mappingProfileKey()
}
