package dev.emumapper.app.core.ui.components

import dev.emumapper.app.core.input.GamepadAction

/*
le format d’un hint affichable
 */
data class ActionHint(
    val action: GamepadAction,
    val label: String
)
