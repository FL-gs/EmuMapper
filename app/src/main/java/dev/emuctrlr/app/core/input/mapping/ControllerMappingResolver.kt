package dev.emuctrlr.app.core.input.mapping

import dev.emuctrlr.app.core.input.ControllerInfo

/**
 * Résout le mapping final d'une manette :
 *
 * mapping final = mapping par défaut + overrides utilisateur
 *
 */
class ControllerMappingResolver(
    private val userOverridesByName: Map<String, ControllerMapping> = emptyMap()
) {
    fun resolve(controller: ControllerInfo): ControllerMapping {
        val key = controller.mappingProfileKey()

        val defaultMapping = DefaultControllerMappings.resolveDefault(
            controllerName = controller.name
        )

        return defaultMapping.mergedWithOverrides(
            overrides = userOverridesByName[key]
        )
    }

    fun resolveAll(controllers: List<ControllerInfo>): List<MappedController> {
        return controllers.map { controller ->
            MappedController(
                controller = controller,
                mapping = resolve(controller)
            )
        }
    }
}
