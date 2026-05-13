package dev.emumapper.app.core.input

import android.os.Build
import dev.emumapper.app.R
import dev.emumapper.app.core.ui.components.ControllerHintStyle

object ControllerDisplay {

    /*
     * Returns the image used to display this controller in the UI.
     */
    fun iconResFor(controller: ControllerInfo): Int {
        return if (controller.isInternal) {
            internalConsoleResForCurrentDevice()
        } else {
            externalControllerResFor(controller.name)
        }
    }

    /*
     * Picks the image for known external controllers.
     */
    private fun externalControllerResFor(name: String): Int {
        val n = name.lowercase()
        return when {
            n.contains("8bitdo ultimate 2c") -> R.drawable.controller_8bitdo_ultimate_2c
            n.contains("8bitdo ultimate") -> R.drawable.controller_8bitdo_ultimate
            n.contains("8bitdo ngc") -> R.drawable.controller_8bitdo_ngc_modkit
            n.contains("8bitdo sn30 pro") -> R.drawable.controller_8bitdo_sn30_pro
            n.contains("nyxi warrior") -> R.drawable.controller_nyxi_warrior
            n.contains("dualsense") -> R.drawable.controller_dualsense
            n.contains("xbox") -> R.drawable.controller_xbox
            else -> R.drawable.controller_placeholder
        }
    }

    /*
     * Returns the name shown in the controller grid.
     */
    fun displayNameFor(controller: ControllerInfo): String {
        return if (controller.isInternal) {
            internalConsoleDisplayNameForCurrentDevice()
        } else {
            controller.name
        }
    }

    /*
     * Returns the hint style used by the bottom hint bar.
     */
    fun hintStyleFor(controller: ControllerInfo): ControllerHintStyle {
        return if (controller.isInternal) {
            internalConsoleHintStyleForCurrentDevice()
        } else {
            externalControllerHintStyleFor(controller.name)
        }
    }

    /*
     * Picks the hint style for known external controllers.
     */
    private fun externalControllerHintStyleFor(name: String): ControllerHintStyle {
        val n = name.lowercase()

        return when {
            n.contains("xbox") -> ControllerHintStyle.XBOX

            n.contains("dualsense") ->
                ControllerHintStyle.DUALSENSE

            n.contains("8bitdo ultimate 2c") ->
                ControllerHintStyle.WIRELESS_ULTIMATE_2C

            n.contains("8bitdo ultimate 2") ->
                ControllerHintStyle.WIRELESS_ULTIMATE_2

            n.contains("8bitdo sn30 pro") ->
                ControllerHintStyle.SN30_PRO
            else ->
                ControllerHintStyle.GENERIC
        }
    }

    /*
     * Picks the hint style for the current Android handheld.
     */
    private fun internalConsoleHintStyleForCurrentDevice(): ControllerHintStyle {
        val manufacturer = Build.MANUFACTURER.orEmpty().lowercase()
        val model = Build.MODEL.orEmpty().lowercase()

        return when {
            manufacturer.contains("ayn") && model.contains("odin2 portal") ->
                ControllerHintStyle.AYN_ODIN_2_PORTAL

            manufacturer.contains("ayn") && (
                    model.contains("odin3") || model.contains("odin 3")
                    ) ->
                ControllerHintStyle.AYN_ODIN_3

            manufacturer.contains("retroid") &&
                    (model.contains("rp5") || model.contains("pocket 5")) ->
                ControllerHintStyle.RETROID_POCKET_5

            manufacturer.contains("retroid") &&
                    (model.contains("rp6") || model.contains("pocket 6")) ->
                ControllerHintStyle.RETROID_POCKET_6

            else ->
                ControllerHintStyle.GENERIC
        }
    }

    /*
     * Picks the image for the current Android handheld.
     */
    private fun internalConsoleResForCurrentDevice(): Int {
        val manufacturer = Build.MANUFACTURER.orEmpty().lowercase()
        val model = Build.MODEL.orEmpty().lowercase()

        return when {
            manufacturer.contains("ayn") && model.contains("odin2 portal") ->
                R.drawable.console_ayn_odin_2_portal

            manufacturer.contains("retroid") && (
                    model.contains("rp5") || model.contains("pocket 5")
                    ) ->
                R.drawable.console_retroid_pocket_5

            manufacturer.contains("retroid") && (
                    model.contains("rp6") || model.contains("pocket 6")
                    ) ->
                R.drawable.console_retroid_pocket_6

            manufacturer.contains("ayn") && (
                    model.contains("odin3") || model.contains("odin 3")
                    ) ->
                R.drawable.console_ayn_odin_3

            else ->
                R.drawable.console_placeholder
        }
    }

    /*
     * Returns the display name for the current Android handheld.
     */
    private fun internalConsoleDisplayNameForCurrentDevice(): String {
        val manufacturer = Build.MANUFACTURER.orEmpty().lowercase()
        val model = Build.MODEL.orEmpty().lowercase()

        return when {
            manufacturer.contains("ayn") && model.contains("odin2 portal") ->
                "AYN Odin 2 Portal"

            manufacturer.contains("ayn") && (
                    model.contains("odin3") || model.contains("odin 3")
                    ) ->
                "AYN Odin 3"

            manufacturer.contains("retroid") && (
                    model.contains("rp5") || model.contains("pocket 5")
                    ) ->
                "Retroid Pocket 5"

            manufacturer.contains("retroid") && (
                    model.contains("rp6") || model.contains("pocket 6")
                    ) ->
                "Retroid Pocket 6"

            else ->
                Build.MODEL.orEmpty().ifBlank { "Internal console" }
        }
    }
}