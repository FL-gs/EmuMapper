package dev.emumapper.app.core.input.mapping

/*
 * Resolves the default controller mapping.
 *
 * For now, the app only has one standard profile.
 * controllerName is intentionally kept so controller-specific profiles
 * can be added later.
 */
object DefaultControllerMappings {

    fun resolveDefault(controllerName: String): ControllerMapping {
        return MappingProfiles.androidStandard
    }
}
