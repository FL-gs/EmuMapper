package dev.emumapper.app.core.ui.components

import dev.emumapper.app.core.input.GamepadAction

/*
 * Hint shown in the bottom hint bar.
 */
data class ActionHint(
    val action: GamepadAction,
    val label: String
)
