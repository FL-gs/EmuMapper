package com.example.pairingapp.core.ui.components

import com.example.pairingapp.core.input.GamepadAction

/*
le format d’un hint affichable
 */
data class ActionHint(
    val action: GamepadAction,
    val label: String
)
