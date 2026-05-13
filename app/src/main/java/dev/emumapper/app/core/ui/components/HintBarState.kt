package dev.emumapper.app.core.ui.components

/*
 * Describes the current UI context used by the hint bar.
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