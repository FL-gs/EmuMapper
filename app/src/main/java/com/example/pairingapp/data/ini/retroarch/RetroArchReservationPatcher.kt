package com.example.pairingapp.data.ini.retroarch

import com.example.pairingapp.core.input.ControllerInfo

object RetroArchReservationPatcher {

    fun patchCfg(
        original: String,
        controllers: List<ControllerInfo>
    ): String {
        val normalized = original
            .replace("\r\n", "\n")
            .replace('\r', '\n')

        val values = linkedMapOf<String, String>()
        val rawLines = mutableListOf<String>()

        normalized.lineSequence().forEach { line ->
            val idx = line.indexOf('=')
            if (idx <= 0) {
                if (line.isNotBlank()) rawLines += line
                return@forEach
            }

            val key = line.substring(0, idx).trim()
            if (key.isEmpty()) {
                if (line.isNotBlank()) rawLines += line
                return@forEach
            }

            if (key !in values) {
                values[key] = line.substring(idx + 1).trim().removeSurrounding("\"")
            }
        }

        val active = controllers.take(4)

        for (slot in 1..4) {
            val typeKey = "input_player${slot}_device_reservation_type"
            val deviceKey = "input_player${slot}_reserved_device"

            val controller = active.getOrNull(slot - 1)

            if (controller != null) {
                val vendor = "%04x".format(controller.vendorId)
                val product = "%04x".format(controller.productId)
                val deviceString = "$vendor:$product ${controller.name}"

                // garde ton comportement actuel
                values[typeKey] = "0"
                values[deviceKey] = ""

                // si un jour tu veux réellement activer la réservation :
                // values[typeKey] = "1"
                // values[deviceKey] = deviceString
            } else {
                values[typeKey] = "0"
                values[deviceKey] = ""
            }
        }

        val handledKeys = buildSet {
            for (slot in 1..4) {
                add("input_player${slot}_device_reservation_type")
                add("input_player${slot}_reserved_device")
            }
        }

        return buildString(normalized.length + 128) {
            values.forEach { (k, v) ->
                if (k in handledKeys) return@forEach
                appendLine("""$k = "$v"""")
            }

            if (isNotEmpty()) appendLine()

            for (slot in 1..4) {
                appendLine(
                    """input_player${slot}_device_reservation_type = "${values.getValue("input_player${slot}_device_reservation_type")}""""
                )
                appendLine(
                    """input_player${slot}_reserved_device = "${values.getValue("input_player${slot}_reserved_device")}""""
                )
            }

            if (isNotEmpty()) {
                setLength(length - 1)
            }
        }
    }
}