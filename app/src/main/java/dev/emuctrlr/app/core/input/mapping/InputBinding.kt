package dev.emuctrlr.app.core.input.mapping

import android.view.KeyEvent
import android.view.MotionEvent

/**
 * Input Android reçu pour une action logique EmuCtrlr.
 *
 * Important :
 * - Les boutons simples viennent souvent de KeyEvent.
 * - Le D-Pad peut venir de KeyEvent OU de MotionEvent axis.
 * - Les gâchettes et les sticks peuvent venir de MotionEvent axis.
 */
sealed interface InputBinding {

    fun displayLabel(): String

    data class Button(
        val keyCode: Int
    ) : InputBinding {
        override fun displayLabel(): String {
            return "${KeyEvent.keyCodeToString(keyCode)} ($keyCode)"
        }
    }

    data class AxisDirection(
        val axis: Int,
        val sign: AxisSign
    ) : InputBinding {
        override fun displayLabel(): String {
            return "${axisName(axis)}${sign.symbol} ($axis${sign.symbol})"
        }
    }

    data class Stick(
        val axisX: Int,
        val axisY: Int
    ) : InputBinding {
        override fun displayLabel(): String {
            return "${axisName(axisX)} / ${axisName(axisY)} ($axisX/$axisY)"
        }
    }
}

fun axisName(axis: Int): String {
    return when (axis) {
        MotionEvent.AXIS_X -> "AXIS_X"
        MotionEvent.AXIS_Y -> "AXIS_Y"
        MotionEvent.AXIS_Z -> "AXIS_Z"
        MotionEvent.AXIS_RZ -> "AXIS_RZ"
        MotionEvent.AXIS_HAT_X -> "AXIS_HAT_X"
        MotionEvent.AXIS_HAT_Y -> "AXIS_HAT_Y"
        MotionEvent.AXIS_LTRIGGER -> "AXIS_LTRIGGER"
        MotionEvent.AXIS_RTRIGGER -> "AXIS_RTRIGGER"
        MotionEvent.AXIS_GAS -> "AXIS_GAS"
        MotionEvent.AXIS_BRAKE -> "AXIS_BRAKE"
        else -> "AXIS_$axis"
    }
}
