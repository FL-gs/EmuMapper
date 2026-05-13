package dev.emumapper.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.emumapper.app.R
import dev.emumapper.app.core.input.GamepadAction

/*
 * Builds the hints to display for the current UI state.
 */
@Composable
fun rememberHintsForState(state: HintBarState): List<ActionHint> {
    val hintNavigate = stringResource(R.string.hint_navigate)
    val hintBack = stringResource(R.string.hint_back)
    val hintChoose = stringResource(R.string.hint_choose)
    val hintOk = stringResource(R.string.hint_enter)
    val hintActivate = stringResource(R.string.hint_activate)
    val hintDeactivate = stringResource(R.string.hint_deactivate)
    val hintSettings = stringResource(R.string.hint_settings)
    val hintHoldToWrite = stringResource(R.string.hint_hold_to_write)
    val hintExit = stringResource(R.string.hint_exit)

    return when (state) {
        HintBarState.None -> emptyList()

        HintBarState.Onboarding -> listOf(
            ActionHint(GamepadAction.NAVIGATE, hintNavigate),
            ActionHint(GamepadAction.CHOOSE, hintChoose),
        )

        HintBarState.OnboardingPickerDialog -> listOf(
            ActionHint(GamepadAction.NAVIGATE, hintNavigate),
            ActionHint(GamepadAction.CHOOSE, hintChoose),
            ActionHint(GamepadAction.BACK, hintBack)
        )

        HintBarState.PairingEmpty -> listOf(
            ActionHint(GamepadAction.SELECT, hintSettings),
            ActionHint(GamepadAction.BACK, hintExit)
        )

        HintBarState.PairingAutoWithControllers -> listOf(
            ActionHint(GamepadAction.SELECT, hintSettings),
            ActionHint(GamepadAction.BACK, hintExit)
        )

        HintBarState.PairingManualWithControllers -> listOf(
            ActionHint(GamepadAction.SELECT, hintSettings),
            ActionHint(GamepadAction.BACK, hintExit)
        )

        HintBarState.SettingsHome -> listOf(
            ActionHint(GamepadAction.NAVIGATE, hintNavigate),
            ActionHint(GamepadAction.CONFIRM, hintOk),
            ActionHint(GamepadAction.BACK, hintBack)
        )

        is HintBarState.AppSettings -> {
            val primary = when (state.focusedZone) {
                AppSettingsFocusZone.SETTINGS_ROW ->
                    ActionHint(GamepadAction.CHOOSE, hintChoose)

                AppSettingsFocusZone.CONTROLLER_BUTTON ->
                    ActionHint(GamepadAction.CONFIRM, hintOk)
            }

            listOf(
                ActionHint(GamepadAction.NAVIGATE, hintNavigate),
                primary,
                ActionHint(GamepadAction.BACK, hintBack)
            )
        }

        is HintBarState.Emulators -> listOf(
            ActionHint(GamepadAction.NAVIGATE, hintNavigate),
            ActionHint(
                GamepadAction.CONFIRM,
                if (state.focusedEnabled) hintDeactivate else hintActivate
            ),
            ActionHint(GamepadAction.BACK, hintBack)
        )

        is HintBarState.Debug -> listOf(
            ActionHint(
                GamepadAction.CONFIRM,
                if (state.enabled) hintDeactivate else hintActivate
            ),
            ActionHint(GamepadAction.BACK, hintBack)
        )
    }
}