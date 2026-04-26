package com.example.pairingapp.data.ini.dolphin

object DolphinCoreIniPatcher {

    private data class Section(
        val header: String,
        val lines: MutableList<String>
    )

    fun patchIni(original: String): String {
        val normalized = original
            .replace("\r\n", "\n")
            .replace('\r', '\n')

        val sections = parseSections(normalized)
        val sectionByHeader = sections.associateByTo(linkedMapOf()) { it.header }

        val coreSection = sectionByHeader["[Core]"] ?: Section("[Core]", mutableListOf()).also {
            sections += it
            sectionByHeader["[Core]"] = it
        }

        patchCoreSection(coreSection)

        return buildString(normalized.length + 128) {
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
                if (currentHeader == null) return@forEach
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

    private fun patchCoreSection(section: Section) {
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

        values["SIDevice0"] = "6"
        values["SIDevice1"] = "6"
        values["SIDevice2"] = "6"
        values["SIDevice3"] = "6"

        val rebuilt = mutableListOf<String>()

        rebuilt += "SIDevice0 = ${values.getValue("SIDevice0")}"
        rebuilt += "SIDevice1 = ${values.getValue("SIDevice1")}"
        rebuilt += "SIDevice2 = ${values.getValue("SIDevice2")}"
        rebuilt += "SIDevice3 = ${values.getValue("SIDevice3")}"

        val handledKeys = setOf(
            "SIDevice0",
            "SIDevice1",
            "SIDevice2",
            "SIDevice3"
        )

        values.forEach { (key, value) ->
            if (key !in handledKeys) {
                rebuilt += "$key = $value"
            }
        }

        section.lines.clear()
        section.lines.addAll(rebuilt)
    }
}