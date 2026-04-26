package com.example.pairingapp.core.ui.components

import com.example.pairingapp.R
import com.example.pairingapp.core.input.GamepadAction

object ActionIconResolver {

    fun iconRes(
        action: GamepadAction,
        style: ControllerHintStyle
    ): Int = when (style) {

        ControllerHintStyle.AYN_ODIN_2_PORTAL,
        ControllerHintStyle.AYN_ODIN_3 -> when (action) {
            GamepadAction.SELECT -> R.drawable.ic_select_odin
            GamepadAction.START -> R.drawable.ic_start_odin
            else -> iconRes(action, ControllerHintStyle.GENERIC)
        }

        ControllerHintStyle.WIRELESS_ULTIMATE_2C,
        ControllerHintStyle.WIRELESS_ULTIMATE_2 -> when (action) {
            GamepadAction.SELECT -> R.drawable.ic_select_8bitdo
            GamepadAction.START -> R.drawable.ic_start_8bitdo
            else -> iconRes(action, ControllerHintStyle.GENERIC)
        }

        ControllerHintStyle.SN30_PRO  -> when (action) {
            GamepadAction.SELECT -> R.drawable.ic_select
            GamepadAction.START -> R.drawable.ic_start
            else -> iconRes(action, ControllerHintStyle.GENERIC)
        }

        else -> when (action) {
            GamepadAction.NAVIGATE -> R.drawable.ic_nav
            GamepadAction.CHOOSE -> R.drawable.ic_choose
            GamepadAction.CONFIRM -> R.drawable.ic_confirm
            GamepadAction.BACK -> R.drawable.ic_back
            GamepadAction.START -> R.drawable.ic_start
            GamepadAction.HOME -> R.drawable.ic_home
            GamepadAction.SELECT -> R.drawable.ic_select
        }
    }
}