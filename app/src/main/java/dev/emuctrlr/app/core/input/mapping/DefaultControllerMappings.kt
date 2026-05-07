package dev.emuctrlr.app.core.input.mapping

/**
 * Résolution du mapping par défaut.
 *
 * Pour l'instant, l'app n'a qu'un seul profil standard.
 * Le paramètre controllerName est conservé volontairement pour pouvoir ajouter plus tard :
 *
 * when {
 *     name.contains("8bitdo ...") -> MappingProfiles.xxx
 *     name.contains("dualsense") -> MappingProfiles.xxx
 *     else -> MappingProfiles.androidStandard
 * }
 */
object DefaultControllerMappings {

    fun resolveDefault(controllerName: String): ControllerMapping {
        return MappingProfiles.androidStandard
    }
}
