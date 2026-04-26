package com.example.pairingapp.data.ini.retroarch

import com.example.pairingapp.core.input.ControllerInfo

object RetroArchControllerConfigPatcher {

    private val mappingTemplate = listOf(
        "input_up_btn" to "h0up",
        "input_down_btn" to "h0down",
        "input_left_btn" to "h0left",
        "input_right_btn" to "h0right",

        "input_b_btn" to "96",
        "input_a_btn" to "97",
        "input_x_btn" to "99",
        "input_y_btn" to "100",

        "input_select_btn" to "109",
        "input_start_btn" to "108",

        "input_l_btn" to "102",
        "input_r_btn" to "103",
        "input_l2_btn" to "104",
        "input_r2_btn" to "105",

        "input_l3_btn" to "106",
        "input_r3_btn" to "107",

        "input_l_x_plus_axis" to "+0",
        "input_l_x_minus_axis" to "-0",
        "input_l_y_plus_axis" to "+1",
        "input_l_y_minus_axis" to "-1",

        "input_r_x_plus_axis" to "+2",
        "input_r_x_minus_axis" to "-2",
        "input_r_y_plus_axis" to "+3",
        "input_r_y_minus_axis" to "-3"
    )

    fun patchCfg(
        original: String,
        controller: ControllerInfo
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

        values["input_driver"] = "android"
        values["input_device"] = controller.name
        values["input_vendor_id"] = controller.vendorId.toString()
        values["input_product_id"] = controller.productId.toString()

        mappingTemplate.forEach { (k, v) ->
            values[k] = v
        }

        val handledKeys = buildSet {
            add("input_driver")
            add("input_device")
            add("input_vendor_id")
            add("input_product_id")
            mappingTemplate.forEach { (k, _) -> add(k) }
        }

        return buildString(normalized.length + 256) {
            appendLine("""input_driver = "${values.getValue("input_driver")}"""")
            appendLine("""input_device = "${values.getValue("input_device")}"""")
            appendLine("""input_vendor_id = "${values.getValue("input_vendor_id")}"""")
            appendLine("""input_product_id = "${values.getValue("input_product_id")}"""")

            mappingTemplate.forEach { (k, _) ->
                appendLine("""$k = "${values.getValue(k)}"""")
            }

            values.forEach { (k, v) ->
                if (k !in handledKeys) {
                    appendLine("""$k = "$v"""")
                }
            }

            if (isNotEmpty()) {
                setLength(length - 1)
            }
        }
    }
}