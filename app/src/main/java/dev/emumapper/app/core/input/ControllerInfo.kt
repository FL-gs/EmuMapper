package dev.emumapper.app.core.input

import android.view.InputDevice

/*
 * Builds ControllerInfo from Android InputDevice data.
 */
data class ControllerInfo(
    val name: String,
    val deviceId: Int,
    val descriptor: String?,
    val controllerNumber: Int?,
    val vendorId: Int,
    val productId: Int,
    val isInternal: Boolean = false,
    val yuzuPort: Int? = null
)

/*
 * Creates a stable key used to identify the internal controller.
 */
fun ControllerInfo.internalProfileKey(): String {
    return "${name}|${descriptor ?: "null"}"
}

fun InputDevice.toControllerInfo(isInternal: Boolean = false): ControllerInfo {
    return ControllerInfo(
        name = name,
        deviceId = id,
        descriptor = descriptor,
        controllerNumber = controllerNumber,
        vendorId = vendorId,
        productId = productId,
        isInternal = isInternal
    )
}

/*
 * Key used to identify a physical controller and avoid duplicates.
 *
 * Priority:
 * 1. controllerNumber, most reliable with Android proxy devices
 * 2. descriptor, stable in most cases
 * 3. deviceId fallback
 */
fun ControllerInfo.deduplicationKey(): String {
    return controllerNumber?.let { "num:$it" }
        ?: descriptor
        ?: "id:$deviceId"
}

/*
 * Checks whether this device is a gamepad/joystick
 */
fun InputDevice.isGamepadDevice(): Boolean {
    if (isVirtual) return false

    val s = sources
    val isGamepad = (s and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
    val isJoystick = (s and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK

    return isGamepad || isJoystick
}

