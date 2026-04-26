package com.example.pairingapp.data.ini.retroarch

object RetroArchMainConfigPatcher {

    fun patchIni(
        original: String,
        autoconfigPath: String
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

        values["joypad_autoconfig_dir"] = autoconfigPath

        return buildString(normalized.length + 64) {
            values.forEach { (k, v) ->
                appendLine("""$k = "$v"""")
            }
            if (isNotEmpty()) {
                setLength(length - 1)
            }
        }
    }
}