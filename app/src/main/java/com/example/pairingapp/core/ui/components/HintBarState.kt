package com.example.pairingapp.core.ui.components

/*
la description du contexte courant

sur quel écran l'utilisateur es
quel sous-état UI est actif
ce qui influence les hints
 */

sealed interface HintBarState {

    data object None : HintBarState

    // Onboarding
    data object Onboarding : HintBarState
    data object OnboardingPickerDialog : HintBarState

    // Pairing
    data object PairingEmpty : HintBarState
    data object PairingAutoWithControllers : HintBarState
    data object PairingManualWithControllers : HintBarState

    // Settings
    data object SettingsHome : HintBarState

    data class AppSettings(
        val focusedZone: AppSettingsFocusZone
    ) : HintBarState

    data class Emulators(
        val focusedEnabled: Boolean
    ) : HintBarState

    data class Debug(
        val enabled: Boolean
    ) : HintBarState
}

enum class AppSettingsFocusZone {
    SETTINGS_ROW,
    CONTROLLER_BUTTON
}