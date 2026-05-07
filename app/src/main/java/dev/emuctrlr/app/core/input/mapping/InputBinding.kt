package dev.emuctrlr.app.core.input.mapping

import android.view.KeyEvent
import android.view.MotionEvent
import java.util.Locale

/**
 * Input Android reçu pour une action logique EmuCtrlr.
 *
 * Important :
 * - Les boutons simples viennent souvent de KeyEvent.
 * - Le D-Pad peut venir de KeyEvent OU de MotionEvent axis.
 * - Les gâchettes et les sticks peuvent venir de MotionEvent axis.
 *
 * displayLabel() sert uniquement à l'affichage UI.
 * Les vraies données utilisées par les mappings restent keyCode / axis / sign.
 */
sealed interface InputBinding {

    fun displayLabel(): String

    data class Button(
        val keyCode: Int
    ) : InputBinding {
        override fun displayLabel(): String {
            return "${keyCodeDisplayName(keyCode)} ($keyCode)"
        }
    }

    data class AxisDirection(
        val axis: Int,
        val sign: AxisSign
    ) : InputBinding {
        override fun displayLabel(): String {
            return "${axisDisplayName(axis)} ${sign.symbol} ($axis${sign.symbol})"
        }
    }

    data class Stick(
        val axisX: Int,
        val axisY: Int
    ) : InputBinding {
        override fun displayLabel(): String {
            return "${axisDisplayName(axisX)} / ${axisDisplayName(axisY)} ($axisX/$axisY)"
        }
    }
}

private fun keyCodeDisplayName(keyCode: Int): String {
    return when (keyCode) {
        KeyEvent.KEYCODE_BUTTON_A -> "Button A"
        KeyEvent.KEYCODE_BUTTON_B -> "Button B"
        KeyEvent.KEYCODE_BUTTON_X -> "Button X"
        KeyEvent.KEYCODE_BUTTON_Y -> "Button Y"

        KeyEvent.KEYCODE_BUTTON_START -> "Start"
        KeyEvent.KEYCODE_BUTTON_SELECT -> "Select"
        KeyEvent.KEYCODE_BUTTON_MODE -> "Mode"

        KeyEvent.KEYCODE_BUTTON_L1 -> "L1"
        KeyEvent.KEYCODE_BUTTON_R1 -> "R1"
        KeyEvent.KEYCODE_BUTTON_L2 -> "L2"
        KeyEvent.KEYCODE_BUTTON_R2 -> "R2"
        KeyEvent.KEYCODE_BUTTON_THUMBL -> "L3"
        KeyEvent.KEYCODE_BUTTON_THUMBR -> "R3"

        KeyEvent.KEYCODE_DPAD_UP -> "D-Pad Up"
        KeyEvent.KEYCODE_DPAD_DOWN -> "D-Pad Down"
        KeyEvent.KEYCODE_DPAD_LEFT -> "D-Pad Left"
        KeyEvent.KEYCODE_DPAD_RIGHT -> "D-Pad Right"
        KeyEvent.KEYCODE_DPAD_CENTER -> "D-Pad Center"

        else -> KeyEvent.keyCodeToString(keyCode).toReadableAndroidName(
            prefix = "KEYCODE_"
        )
    }
}

fun axisName(axis: Int): String {
    return axisDisplayName(axis)
}

private fun axisDisplayName(axis: Int): String {
    return when (axis) {
        MotionEvent.AXIS_X -> "Left Stick X"
        MotionEvent.AXIS_Y -> "Left Stick Y"
        MotionEvent.AXIS_Z -> "Right Stick X"
        MotionEvent.AXIS_RZ -> "Right Stick Y"

        MotionEvent.AXIS_HAT_X -> "D-Pad X"
        MotionEvent.AXIS_HAT_Y -> "D-Pad Y"

        MotionEvent.AXIS_LTRIGGER -> "Left Trigger"
        MotionEvent.AXIS_RTRIGGER -> "Right Trigger"
        MotionEvent.AXIS_GAS -> "Gas"
        MotionEvent.AXIS_BRAKE -> "Brake"

        else -> "Axis $axis"
    }
}

private fun String.toReadableAndroidName(prefix: String): String {
    return removePrefix(prefix)
        .replace("_", " ")
        .lowercase(Locale.ROOT)
        .replaceFirstChar { char ->
            if (char.isLowerCase()) {
                char.titlecase(Locale.ROOT)
            } else {
                char.toString()
            }
        }
}
