package dev.emuctrlr.app.core.input

import android.view.KeyEvent

enum class PadKey { UP, DOWN, LEFT, RIGHT, A, B, SELECT, START, OTHER }

fun mapKeyEvent(event: KeyEvent): PadKey {
    return when (event.keyCode) {
        KeyEvent.KEYCODE_DPAD_UP -> PadKey.UP
        KeyEvent.KEYCODE_DPAD_DOWN -> PadKey.DOWN
        KeyEvent.KEYCODE_DPAD_LEFT -> PadKey.LEFT
        KeyEvent.KEYCODE_DPAD_RIGHT -> PadKey.RIGHT

        KeyEvent.KEYCODE_BUTTON_A,
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_ENTER -> PadKey.A

        KeyEvent.KEYCODE_BUTTON_B,
        KeyEvent.KEYCODE_BACK,
        KeyEvent.KEYCODE_ESCAPE -> PadKey.B

        // Select / View / Back (Xbox)
        KeyEvent.KEYCODE_BUTTON_SELECT -> PadKey.SELECT

        KeyEvent.KEYCODE_BUTTON_START -> PadKey.START

        else -> PadKey.OTHER
    }
}
