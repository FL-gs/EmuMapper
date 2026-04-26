package com.example.pairingapp.data.ini.dolphin

import com.example.pairingapp.core.input.ControllerInfo

object DolphinGcpadIniPatcher {

    private data class Section(
        val header: String,
        val lines: MutableList<String>
    )

    private val mappingTemplate: List<Pair<String, String>> = listOf(
        "Buttons/A" to "`Button A`",
        "Buttons/B" to "`Button B`",
        "Buttons/X" to "`Button X`",
        "Buttons/Y" to "`Button Y`",
        "Buttons/Z" to "Select",
        "Buttons/Start" to "Start",

        "Main Stick/Up" to "`Axis 1-`",
        "Main Stick/Down" to "`Axis 1+`",
        "Main Stick/Left" to "`Axis 0-`",
        "Main Stick/Right" to "`Axis 0+`",

        "C-Stick/Up" to "`Axis 14-`",
        "C-Stick/Down" to "`Axis 14+`",
        "C-Stick/Left" to "`Axis 11-`",
        "C-Stick/Right" to "`Axis 11+`",

        "Triggers/L" to "`Axis 23+`",
        "Triggers/R" to "`Axis 22+`",
        "Triggers/L-Analog" to "`Axis 23+`",
        "Triggers/R-Analog" to "`Axis 22+`",

        "D-Pad/Up" to "`Axis 16-`",
        "D-Pad/Down" to "`Axis 16+`",
        "D-Pad/Left" to "`Axis 15-`",
        "D-Pad/Right" to "`Axis 15+`",
    )

    fun patchIni(original: String, controllers: List<ControllerInfo>): String {
        val normalized = original
            .replace("\r\n", "\n")
            .replace('\r', '\n')

        val sections = parseSections(normalized)
        val sectionByHeader = sections.associateByTo(linkedMapOf()) { it.header }

        controllers.take(4).forEachIndexed { index, controller ->
            val slot = index + 1
            val header = "[GCPad$slot]"
            val section = sectionByHeader[header] ?: Section(header, mutableListOf()).also {
                sections += it
                sectionByHeader[header] = it
            }

            patchSection(section, controller)
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
                    // lignes avant première section : on les ignore comme ton patcher actuel le fait de facto
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

    private fun patchSection(section: Section, controller: ControllerInfo) {
        val values = linkedMapOf<String, String>()

        // Parse existant en gardant la première occurrence de chaque clé
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

        val number = controller.controllerNumber ?: controller.deviceId
        val deviceValue = "Android/$number/${controller.name}"

        values["Device"] = deviceValue

        mappingTemplate.forEach { (key, value) ->
            values[key] = value
        }

        // On reconstruit la section dans un ordre stable:
        // 1. Device
        // 2. Rumble/Motor si présent dans l'ancien fichier
        // 3. mapping template
        // 4. autres clés restantes non reconnues
        val rebuilt = mutableListOf<String>()

        rebuilt += "Device = ${values.getValue("Device")}"

        val existingRumble = values["Rumble/Motor"]
        if (existingRumble != null) {
            rebuilt += "Rumble/Motor = $existingRumble"
        }

        mappingTemplate.forEach { (key, _) ->
            rebuilt += "$key = ${values.getValue(key)}"
        }

        val handledKeys = buildSet {
            add("Device")
            add("Rumble/Motor")
            mappingTemplate.forEach { (key, _) -> add(key) }
        }

        values.forEach { (key, value) ->
            if (key !in handledKeys) {
                rebuilt += "$key = $value"
            }
        }

        section.lines.clear()
        section.lines.addAll(rebuilt)
    }
}