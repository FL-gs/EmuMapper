package dev.emuctrlr.app.core.ui.components

import dev.emuctrlr.app.core.input.GamepadAction

/*
le format d’un hint affichable
 */
data class ActionHint(
    val action: GamepadAction,
    val label: String
)
