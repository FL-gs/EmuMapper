package dev.emumapper.app.data.ini.dolphin

import android.view.KeyEvent
import dev.emumapper.app.core.input.mapping.AxisSign
import dev.emumapper.app.core.input.mapping.EmuControl
import dev.emumapper.app.core.input.mapping.InputBinding
import dev.emumapper.app.core.input.mapping.MappedController

object DolphinGcpadIniPatcher {

    private data class Section(
        val header: String,
        val lines: MutableList<String>
    )

    private data class StickDirections(
        val up: String,
        val down: String,
        val left: String,
        val right: String
    )

    fun patchIni(original: String, controllers: List<MappedController>): String {
        val normalized = original
            .replace("\r\n", "\n")
            .replace('\r', '\n')

        val sections = parseSections(normalized)
        val sectionByHeader = sections.associateByTo(linkedMapOf()) { it.header }

        controllers.take(4).forEachIndexed { index, mappedController ->
            val slot = index + 1
            val header = "[GCPad$slot]"
            val section = sectionByHeader[header] ?: Section(header, mutableListOf()).also {
                sections += it
                sectionByHeader[header] = it
            }

            patchSection(section, mappedController)
        }

        return buildString(normalized.length + 512) {
            sections.forEachIndexed { sectionIndex, section ->
                append(section.header)
                append('\n')

                section.lines.forEachIndexed { lineIndex, line ->
                    append(line)
                    if (lineIndex != section.lines.lastIndex) append('\n')
                }

                if (sectionIndex != sections.lastIndex) append('\n')
                if (sectionIndex != sections.lastIndex) append('\n')
            }
        }
    }

    private fun parseSections(content: String): MutableList<Section> {
        val sections = mutableListOf<Section>()

        var currentHeader: String? = null
        var currentLines = mutableListOf<String>()

        content.lineSequence().forEach { rawLine ->
            val line = rawLine.trimEnd()

            if (isHeader(line)) {
                if (currentHeader != null) {
                    sections += Section(currentHeader!!, currentLines)
                }
                currentHeader = line.trim()
                currentLines = mutableListOf()
            } else {
                if (currentHeader == null) {
                    return@forEach
                }
                currentLines += line
            }
        }

        if (currentHeader != null) {
            sections += Section(currentHeader!!, currentLines)
        }

        return sections
    }

    private fun isHeader(line: String): Boolean {
        val trimmed = line.trim()
        return trimmed.startsWith("[") && trimmed.endsWith("]") && trimmed.length >= 3
    }

    private fun patchSection(section: Section, mappedController: MappedController) {
        val values = linkedMapOf<String, String>()

        section.lines.forEach { line ->
            val idx = line.indexOf('=')
            if (idx <= 0) return@forEach

            val key = line.substring(0, idx).trim()
            if (key.isEmpty()) return@forEach

            if (key !in values) {
                val value = line.substring(idx + 1).trim()
                values[key] = value
            }
        }

        val controller = mappedController.controller
        val number = controller.controllerNumber ?: controller.deviceId
        val deviceValue = "Android/$number/${controller.name}"
        val mappingLines = mappingLinesFor(mappedController)

        values["Device"] = deviceValue

        mappingLines.forEach { (key, value) ->
            values[key] = value
        }

        val rebuilt = mutableListOf<String>()

        rebuilt += "Device = ${values.getValue("Device")}"

        val existingRumble = values["Rumble/Motor"]
        if (existingRumble != null) {
            rebuilt += "Rumble/Motor = $existingRumble"
        }

        mappingLines.forEach { (key, _) ->
            rebuilt += "$key = ${values.getValue(key)}"
        }

        val handledKeys = buildSet {
            add("Device")
            add("Rumble/Motor")
            mappingLines.forEach { (key, _) -> add(key) }
        }

        values.forEach { (key, value) ->
            if (key !in handledKeys) {
                rebuilt += "$key = $value"
            }
        }

        section.lines.clear()
        section.lines.addAll(rebuilt)
    }

    private fun mappingLinesFor(mappedController: MappedController): List<Pair<String, String>> {
        val mapping = mappedController.mapping

        val mainStick = stickDirections(
            binding = mapping.bindingFor(EmuControl.LEFT_STICK),
            defaultAxisX = 0,
            defaultAxisY = 1
        )

        val cStick = stickDirections(
            binding = mapping.bindingFor(EmuControl.RIGHT_STICK),
            defaultAxisX = 11,
            defaultAxisY = 14
        )

        val lTrigger = triggerExpression(
            binding = mapping.bindingFor(EmuControl.L2),
            fallback = axisExpression(axis = 23, sign = AxisSign.POSITIVE)
        )

        val rTrigger = triggerExpression(
            binding = mapping.bindingFor(EmuControl.R2),
            fallback = axisExpression(axis = 22, sign = AxisSign.POSITIVE)
        )

        return listOf(
            "Buttons/A" to buttonExpression(mapping.bindingFor(EmuControl.A), fallback = "`Button A`"),
            "Buttons/B" to buttonExpression(mapping.bindingFor(EmuControl.B), fallback = "`Button B`"),
            "Buttons/X" to buttonExpression(mapping.bindingFor(EmuControl.X), fallback = "`Button X`"),
            "Buttons/Y" to buttonExpression(mapping.bindingFor(EmuControl.Y), fallback = "`Button Y`"),
            "Buttons/Z" to buttonExpression(mapping.bindingFor(EmuControl.SELECT), fallback = "Select"),
            "Buttons/Start" to buttonExpression(mapping.bindingFor(EmuControl.START), fallback = "Start"),

            "Main Stick/Up" to mainStick.up,
            "Main Stick/Down" to mainStick.down,
            "Main Stick/Left" to mainStick.left,
            "Main Stick/Right" to mainStick.right,

            "C-Stick/Up" to cStick.up,
            "C-Stick/Down" to cStick.down,
            "C-Stick/Left" to cStick.left,
            "C-Stick/Right" to cStick.right,

            "Triggers/L" to lTrigger,
            "Triggers/R" to rTrigger,
            "Triggers/L-Analog" to lTrigger,
            "Triggers/R-Analog" to rTrigger,

            "D-Pad/Up" to directionExpression(
                binding = mapping.bindingFor(EmuControl.DPAD_UP),
                fallback = axisExpression(axis = 16, sign = AxisSign.NEGATIVE)
            ),
            "D-Pad/Down" to directionExpression(
                binding = mapping.bindingFor(EmuControl.DPAD_DOWN),
                fallback = axisExpression(axis = 16, sign = AxisSign.POSITIVE)
            ),
            "D-Pad/Left" to directionExpression(
                binding = mapping.bindingFor(EmuControl.DPAD_LEFT),
                fallback = axisExpression(axis = 15, sign = AxisSign.NEGATIVE)
            ),
            "D-Pad/Right" to directionExpression(
                binding = mapping.bindingFor(EmuControl.DPAD_RIGHT),
                fallback = axisExpression(axis = 15, sign = AxisSign.POSITIVE)
            ),
        )
    }

    private fun stickDirections(
        binding: InputBinding?,
        defaultAxisX: Int,
        defaultAxisY: Int
    ): StickDirections {
        val stick = binding as? InputBinding.Stick
        val axisX = stick?.axisX ?: defaultAxisX
        val axisY = stick?.axisY ?: defaultAxisY

        return StickDirections(
            up = axisExpression(axis = axisY, sign = AxisSign.NEGATIVE),
            down = axisExpression(axis = axisY, sign = AxisSign.POSITIVE),
            left = axisExpression(axis = axisX, sign = AxisSign.NEGATIVE),
            right = axisExpression(axis = axisX, sign = AxisSign.POSITIVE)
        )
    }

    private fun directionExpression(
        binding: InputBinding?,
        fallback: String
    ): String {
        return when (binding) {
            is InputBinding.AxisDirection -> axisExpression(binding.axis, binding.sign)
            is InputBinding.Button -> buttonExpression(binding, fallback)
            is InputBinding.Stick -> fallback
            null -> fallback
        }
    }

    private fun triggerExpression(
        binding: InputBinding?,
        fallback: String
    ): String {
        return when (binding) {
            is InputBinding.AxisDirection -> axisExpression(binding.axis, binding.sign)
            is InputBinding.Button -> when (binding.keyCode) {
                // Conserve le comportement Dolphin actuel pour le profil Android standard.
                KeyEvent.KEYCODE_BUTTON_L2 -> axisExpression(axis = 23, sign = AxisSign.POSITIVE)
                KeyEvent.KEYCODE_BUTTON_R2 -> axisExpression(axis = 22, sign = AxisSign.POSITIVE)
                else -> buttonExpression(binding, fallback)
            }
            is InputBinding.Stick -> fallback
            null -> fallback
        }
    }

    private fun buttonExpression(
        binding: InputBinding?,
        fallback: String
    ): String {
        return when (binding) {
            is InputBinding.Button -> keyCodeToDolphinButton(binding.keyCode)
            is InputBinding.AxisDirection -> axisExpression(binding.axis, binding.sign)
            is InputBinding.Stick -> fallback
            null -> fallback
        }
    }

    private fun axisExpression(axis: Int, sign: AxisSign): String {
        return "`Axis $axis${sign.symbol}`"
    }

    private fun keyCodeToDolphinButton(keyCode: Int): String {
        return when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> "`Button A`"
            KeyEvent.KEYCODE_BUTTON_B -> "`Button B`"
            KeyEvent.KEYCODE_BUTTON_X -> "`Button X`"
            KeyEvent.KEYCODE_BUTTON_Y -> "`Button Y`"
            KeyEvent.KEYCODE_BUTTON_L1 -> "`Button L1`"
            KeyEvent.KEYCODE_BUTTON_R1 -> "`Button R1`"
            KeyEvent.KEYCODE_BUTTON_L2 -> "`Button L2`"
            KeyEvent.KEYCODE_BUTTON_R2 -> "`Button R2`"
            KeyEvent.KEYCODE_BUTTON_THUMBL -> "`Button ThumbL`"
            KeyEvent.KEYCODE_BUTTON_THUMBR -> "`Button ThumbR`"
            KeyEvent.KEYCODE_BUTTON_START -> "Start"
            KeyEvent.KEYCODE_BUTTON_SELECT -> "Select"
            KeyEvent.KEYCODE_DPAD_UP -> "`DPad Up`"
            KeyEvent.KEYCODE_DPAD_DOWN -> "`DPad Down`"
            KeyEvent.KEYCODE_DPAD_LEFT -> "`DPad Left`"
            KeyEvent.KEYCODE_DPAD_RIGHT -> "`DPad Right`"
            else -> "`${KeyEvent.keyCodeToString(keyCode)}`"
        }
    }
}
