package dev.emumapper.app.core.input

/**
 * Retourne le label à afficher pour la manette interne.
 *
 * La clé stockée dans le DataStore est de la forme :
 *      "name|descriptor"
 *
 * Si la manette est actuellement détectée (présente dans `choices`),
 * on utilise son label réel.
 *
 * Sinon (ex: profil interne différent, manette non active),
 * on fallback sur le nom contenu dans la clé afin d'éviter d'afficher
 * "Inconnu" ou "Aucun".
 */
fun internalControllerLabel(
    key: String?,
    choices: List<DeviceChoice>,
    noneLabel: String
): String {
    if (key == null) return noneLabel

    return choices.firstOrNull { it.key == key }?.label
        ?: key.substringBefore("|")
}
