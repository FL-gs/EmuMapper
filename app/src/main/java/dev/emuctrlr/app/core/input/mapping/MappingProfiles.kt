package dev.emuctrlr.app.core.input.mapping

import android.view.KeyEvent
import android.view.MotionEvent

/**
 * Profils de mapping intégrés à l'app.
 *
 * V1 : un seul profil par défaut.
 * Plus tard, possibilité d'ajouter xboxLike, nintendoLike, dualSenseLike, etc.
 */
object MappingProfiles {

    val androidStandard: ControllerMapping = ControllerMapping(
        bindings = linkedMapOf(
            EmuControl.A to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_A),
            EmuControl.B to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_B),
            EmuControl.X to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_X),
            EmuControl.Y to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_Y),

            EmuControl.START to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_START),
            EmuControl.SELECT to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_SELECT),

            EmuControl.L1 to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_L1),
            EmuControl.R1 to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_R1),
            EmuControl.L2 to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_L2),
            EmuControl.R2 to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_R2),
            EmuControl.L3 to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_THUMBL),
            EmuControl.R3 to InputBinding.Button(KeyEvent.KEYCODE_BUTTON_THUMBR),

            EmuControl.DPAD_UP to InputBinding.AxisDirection(
                axis = MotionEvent.AXIS_HAT_Y,
                sign = AxisSign.NEGATIVE
            ),
            EmuControl.DPAD_DOWN to InputBinding.AxisDirection(
                axis = MotionEvent.AXIS_HAT_Y,
                sign = AxisSign.POSITIVE
            ),
            EmuControl.DPAD_LEFT to InputBinding.AxisDirection(
                axis = MotionEvent.AXIS_HAT_X,
                sign = AxisSign.NEGATIVE
            ),
            EmuControl.DPAD_RIGHT to InputBinding.AxisDirection(
                axis = MotionEvent.AXIS_HAT_X,
                sign = AxisSign.POSITIVE
            ),

            EmuControl.LEFT_STICK to InputBinding.Stick(
                axisX = MotionEvent.AXIS_X,
                axisY = MotionEvent.AXIS_Y
            ),
            EmuControl.RIGHT_STICK to InputBinding.Stick(
                axisX = MotionEvent.AXIS_Z,
                axisY = MotionEvent.AXIS_RZ
            )
        )
    )
}
