package dev.emuctrlr.app.core.input

import android.view.InputDevice

/*
Kt qui prend les infos avec deviceInput.
Possibilité d'ajouter d'autre info :
vendorid
productid
haskeys
...
https://developer.android.com/reference/android/view/InputDevice
A chaque rajout dans data class ControllerInfo, rajouter aussi dans fun InputDevice.toControllerInfo(): ControllerInfo {
*/

data class ControllerInfo(
    val name: String,
    val deviceId: Int,
    val descriptor: String?,
    val controllerNumber: Int?,
    val vendorId: Int,
    val productId: Int,
    val isInternal: Boolean = false,

    // Port Android/Yuzu-like utilisé par Eden/Citron
    val yuzuPort: Int? = null
)

/**
 * Créer une clé stable pour pour identifier une manette interne
 * (descriptor peut être null)
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

/**
 * Clé utilisée pour identifier une manette physique et éviter les doublons.
 *
 * Priorité :
 * 1. controllerNumber (le plus fiable pour les proxies Android)
 * 2. descriptor (stable dans la plupart des cas)
 * 3. deviceId fallback
 */
fun ControllerInfo.deduplicationKey(): String {
    return controllerNumber?.let { "num:$it" }
        ?: descriptor
        ?: "id:$deviceId"
}

/**
 * Vérifie si le device est une manette et pas un device genre clavier, casque etc...
 */
fun InputDevice.isGamepadDevice(): Boolean {
    if (isVirtual) return false

    val s = sources
    val isGamepad = (s and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
    val isJoystick = (s and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK

    return isGamepad || isJoystick
}

