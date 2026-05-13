package dev.emumapper.app.core.input

/*
 * Returns the display name for the selected internal controller.
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
