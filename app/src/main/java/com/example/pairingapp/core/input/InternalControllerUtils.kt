package com.example.pairingapp.core.input

/**
 * Retourne le label à afficher pour une manette interne.
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


/**
 * Filtre les choix disponibles pour éviter qu'une même manette soit sélectionnée
 * à la fois comme manette interne 1 et manette interne 2.
 *
 * Règles :
 * - garde toujours "Aucun" (key == null)
 * - garde toujours la valeur actuellement sélectionnée dans le slot édité
 * - exclut la valeur déjà utilisée par l'autre slot
 */
fun availableInternalControllerChoices(
    choices: List<DeviceChoice>,
    selectedSlot: Int,
    internal1: String?,
    internal2: String?
): List<DeviceChoice> {
    val currentKey = if (selectedSlot == 0) internal1 else internal2
    val excludedKey = if (selectedSlot == 0) internal2 else internal1

    return choices.filter { choice ->
        choice.key == null ||
                choice.key == currentKey ||
                choice.key != excludedKey
    }
}
