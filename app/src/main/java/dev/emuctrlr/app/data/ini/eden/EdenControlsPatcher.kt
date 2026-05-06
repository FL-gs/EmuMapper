package dev.emuctrlr.app.data.ini.eden

import dev.emuctrlr.app.core.input.ControllerInfo
import dev.emuctrlr.app.core.input.deduplicationKey

object EdenControlsPatcher {

    private sealed interface LineEntry {
        data class Raw(val text: String) : LineEntry
        data class KeyValue(val key: String, var value: String) : LineEntry
    }

    private fun quoted(value: String): String = "\"$value\""

    private fun buttonValue(
        button: Int,
        display: String,
        guid: String,
        port: Int
    ): String {
        return quoted("button:$button,display:$display,guid:$guid,port:$port,engine:android")
    }

    private fun axisButtonValue(
        axis: Int,
        invert: String,
        display: String,
        guid: String,
        port: Int
    ): String {
        return quoted("invert:$invert,axis:$axis,threshold:0.5,guid:$guid,port:$port,engine:android,display:$display")
    }

    private fun stickValue(
        axisX: Int,
        axisY: Int,
        display: String,
        guid: String,
        port: Int
    ): String {
        return quoted("invert_y:-,invert_x:+,offset_y:0,offset_x:0,engine:android,axis_y:$axisY,axis_x:$axisX,guid:$guid,port:$port,display:$display")
    }

    private fun parseEntries(original: String): Pair<MutableList<LineEntry>, MutableMap<String, Int>> {
        val entries = mutableListOf<LineEntry>()
        val keyIndex = mutableMapOf<String, Int>()

        original
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .lineSequence()
            .forEach { line ->
                val separatorIndex = line.indexOf('=')

                if (separatorIndex <= 0) {
                    entries += LineEntry.Raw(line)
                    return@forEach
                }

                val rawKey = line.substring(0, separatorIndex).trim()
                if (rawKey.isEmpty()) {
                    entries += LineEntry.Raw(line)
                    return@forEach
                }

                val rawValue = line.substring(separatorIndex + 1).trim()

                if (keyIndex.containsKey(rawKey)) {
                    // même comportement que ton ancien code:
                    // on garde la première occurrence et on ignore les doublons
                    return@forEach
                }

                keyIndex[rawKey] = entries.size
                entries += LineEntry.KeyValue(
                    key = rawKey,
                    value = rawValue
                )
            }

        return entries to keyIndex
    }

    private fun upsertLine(
        entries: MutableList<LineEntry>,
        keyIndex: MutableMap<String, Int>,
        key: String,
        value: String
    ) {
        val index = keyIndex[key]
        if (index != null) {
            val existing = entries[index]
            if (existing is LineEntry.KeyValue) {
                existing.value = value
            }
            return
        }

        keyIndex[key] = entries.size
        entries += LineEntry.KeyValue(key = key, value = value)
    }

    fun patchIni(
        original: String,
        controllers: List<ControllerInfo>,
        edenDevices: List<EdenPortEntry>
    ): String {
        val (entries, keyIndex) = parseEntries(original)
        val edenMap = edenDevices.associateBy { it.controller.deduplicationKey() }

        for (playerIndex in 0..9) {
            val controller = controllers.getOrNull(playerIndex)
            val eden = controller?.let { edenMap[it.deduplicationKey()] }

            val connected = (controller != null && eden != null)

            upsertLine(entries, keyIndex, "player_${playerIndex}_connected\\default", "false")
            upsertLine(
                entries,
                keyIndex,
                "player_${playerIndex}_connected",
                if (connected) "true" else "false"
            )

            if (!connected || controller == null || eden == null) {
                continue
            }

            val display = eden.display
            val guid = eden.guid
            val port = eden.port

            upsertLine(entries, keyIndex, "player_${playerIndex}_button_a", buttonValue(96, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_b", buttonValue(97, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_x", buttonValue(99, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_y", buttonValue(100, display, guid, port))

            upsertLine(entries, keyIndex, "player_${playerIndex}_button_l", buttonValue(102, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_r", buttonValue(103, display, guid, port))

            upsertLine(entries, keyIndex, "player_${playerIndex}_button_zl", buttonValue(104, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_zr", buttonValue(105, display, guid, port))

            upsertLine(entries, keyIndex, "player_${playerIndex}_button_lstick", buttonValue(106, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_rstick", buttonValue(107, display, guid, port))

            upsertLine(entries, keyIndex, "player_${playerIndex}_button_plus", buttonValue(108, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_minus", buttonValue(109, display, guid, port))

            upsertLine(entries, keyIndex, "player_${playerIndex}_button_dleft", axisButtonValue(15, "-", display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_dup", axisButtonValue(16, "-", display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_dright", axisButtonValue(15, "+", display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_button_ddown", axisButtonValue(16, "+", display, guid, port))

            upsertLine(entries, keyIndex, "player_${playerIndex}_lstick", stickValue(0, 1, display, guid, port))
            upsertLine(entries, keyIndex, "player_${playerIndex}_rstick", stickValue(11, 14, display, guid, port))
        }

        // Comportement conservé: player_8 reprend la config de player_0
        val p0 = controllers.getOrNull(0)
        val eden0 = p0?.let { edenMap[it.deduplicationKey()] }

        if (p0 != null && eden0 != null) {
            val display = eden0.display
            val guid = eden0.guid
            val port = eden0.port

            upsertLine(entries, keyIndex, "player_8_button_a", buttonValue(96, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_b", buttonValue(97, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_x", buttonValue(99, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_y", buttonValue(100, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_l", buttonValue(102, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_r", buttonValue(103, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_zl", axisButtonValue(17, "+", display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_zr", axisButtonValue(18, "+", display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_lstick", buttonValue(106, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_rstick", buttonValue(107, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_plus", buttonValue(108, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_minus", buttonValue(109, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_dleft", axisButtonValue(15, "-", display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_dup", axisButtonValue(16, "-", display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_dright", axisButtonValue(15, "+", display, guid, port))
            upsertLine(entries, keyIndex, "player_8_button_ddown", axisButtonValue(16, "+", display, guid, port))
            upsertLine(entries, keyIndex, "player_8_lstick", stickValue(0, 1, display, guid, port))
            upsertLine(entries, keyIndex, "player_8_rstick", stickValue(11, 14, display, guid, port))
        }

        val builder = StringBuilder(original.length + 1024)

        entries.forEachIndexed { index, entry ->
            when (entry) {
                is LineEntry.Raw -> builder.append(entry.text)
                is LineEntry.KeyValue -> {
                    builder.append(entry.key)
                    builder.append('=')
                    builder.append(entry.value)
                }
            }

            if (index != entries.lastIndex) {
                builder.append('\n')
            }
        }

        return builder.toString()
    }
}