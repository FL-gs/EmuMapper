package dev.emumapper.app.core.input.mapping

import dev.emumapper.app.core.input.ControllerInfo

/**
 * Résout le mapping final d'une manette :
 *
 * mapping final = mapping par défaut + overrides utilisateur
 *
 * Les overrides sont passés au moment de la résolution pour suivre les changements
 * DataStore sans recréer le resolver.
 */
class ControllerMappingResolver {

    fun resolve(
        controller: ControllerInfo,
        userOverridesByName: Map<String, ControllerMapping> = emptyMap()
    ): ControllerMapping {
        val key = controller.mappingProfileKey()

        val defaultMapping = DefaultControllerMappings.resolveDefault(
            controllerName = controller.name
        )

        return defaultMapping.mergedWithOverrides(
            overrides = userOverridesByName[key]
        )
    }

    fun resolveAll(
        controllers: List<ControllerInfo>,
        userOverridesByName: Map<String, ControllerMapping> = emptyMap()
    ): List<MappedController> {
        return controllers.map { controller ->
            MappedController(
                controller = controller,
                mapping = resolve(
                    controller = controller,
                    userOverridesByName = userOverridesByName
                )
            )
        }
    }
}
