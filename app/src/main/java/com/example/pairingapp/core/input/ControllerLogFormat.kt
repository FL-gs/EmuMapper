package com.example.pairingapp.core.input

import android.view.InputDevice

private fun String?.shortDescriptor(): String =
    this?.take(8) ?: "-"

fun ControllerInfo.toLogLine(slot: Int? = null): String {
    val prefix = slot?.let { "P$it " } ?: ""
    return "$prefix$name | dev=$deviceId | num=${controllerNumber ?: "-"} | desc=${descriptor.shortDescriptor()}"
}

fun List<ControllerInfo>.toLogBlock(): String {
    if (isEmpty()) return "  - none"

    return mapIndexed { index, controller ->
        "  - ${controller.toLogLine(slot = index + 1)}"
    }.joinToString(separator = "\n")
}

fun InputDevice.toDeviceLogLine(): String {
    return "$name | id=$id | num=${controllerNumber ?: "-"} | gamepad=${isGamepadDevice()} | virtual=$isVirtual | desc=${descriptor.shortDescriptor()}"
}
