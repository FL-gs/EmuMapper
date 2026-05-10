package dev.emuctrlr.app.data.ini.yuzulike

import dev.emuctrlr.app.core.input.mapping.EmuControl
import dev.emuctrlr.app.core.input.mapping.InputBinding
import dev.emuctrlr.app.core.input.mapping.MappedController
import dev.emuctrlr.app.core.utils.AppLogger
import dev.emuctrlr.app.core.utils.LogTags

object YuzuControlsPatcher {

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
        controllers: List<MappedController>
    ): String {
        val (entries, keyIndex) = parseEntries(original)

        for (playerIndex in 0..9) {
            val mappedController = controllers.getOrNull(playerIndex)
            val yuzuEntry = mappedController?.controller?.yuzuControllerEntryOrNull()

            if (mappedController != null && yuzuEntry != null) {
                AppLogger.d(
                    LogTags.INI,
                    "yuzu input | player=$playerIndex | name=${mappedController.controller.name} | port=${yuzuEntry.port} | guid=${yuzuEntry.guid}"
                )
            }

            val connected = (mappedController != null && yuzuEntry != null)

            upsertLine(entries, keyIndex, "player_${playerIndex}_connected\\default", "false")
            upsertLine(
                entries,
                keyIndex,
                "player_${playerIndex}_connected",
                if (connected) "true" else "false"
            )

            if (mappedController == null || yuzuEntry == null) {
                continue
            }

            writeControllerBindings(
                entries = entries,
                keyIndex = keyIndex,
                playerIndex = playerIndex,
                mappedController = mappedController,
                yuzuEntry = yuzuEntry
            )
        }

        // player_8 reprend la config de player_0.
        val p0 = controllers.getOrNull(0)
        val p0yuzuEntry = p0?.controller?.yuzuControllerEntryOrNull()

        if (p0 != null && p0yuzuEntry != null) {
            writeControllerBindings(
                entries = entries,
                keyIndex = keyIndex,
                playerIndex = 8,
                mappedController = p0,
                yuzuEntry = p0yuzuEntry
            )
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

    private fun writeControllerBindings(
        entries: MutableList<LineEntry>,
        keyIndex: MutableMap<String, Int>,
        playerIndex: Int,
        mappedController: MappedController,
        yuzuEntry: YuzuControllerEntry
    ) {
        val display = yuzuEntry.display
        val guid = yuzuEntry.guid
        val port = yuzuEntry.port
        val mapping = mappedController.mapping

        upsertControl(entries, keyIndex, playerIndex, "button_a", mapping.bindingFor(EmuControl.A), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_b", mapping.bindingFor(EmuControl.B), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_x", mapping.bindingFor(EmuControl.X), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_y", mapping.bindingFor(EmuControl.Y), display, guid, port)

        upsertControl(entries, keyIndex, playerIndex, "button_l", mapping.bindingFor(EmuControl.L1), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_r", mapping.bindingFor(EmuControl.R1), display, guid, port)

        upsertControl(entries, keyIndex, playerIndex, "button_zl", mapping.bindingFor(EmuControl.L2), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_zr", mapping.bindingFor(EmuControl.R2), display, guid, port)

        upsertControl(entries, keyIndex, playerIndex, "button_lstick", mapping.bindingFor(EmuControl.L3), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_rstick", mapping.bindingFor(EmuControl.R3), display, guid, port)

        upsertControl(entries, keyIndex, playerIndex, "button_plus", mapping.bindingFor(EmuControl.START), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_minus", mapping.bindingFor(EmuControl.SELECT), display, guid, port)

        upsertControl(entries, keyIndex, playerIndex, "button_dleft", mapping.bindingFor(EmuControl.DPAD_LEFT), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_dup", mapping.bindingFor(EmuControl.DPAD_UP), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_dright", mapping.bindingFor(EmuControl.DPAD_RIGHT), display, guid, port)
        upsertControl(entries, keyIndex, playerIndex, "button_ddown", mapping.bindingFor(EmuControl.DPAD_DOWN), display, guid, port)

        upsertStick(entries, keyIndex, playerIndex, "lstick", mapping.bindingFor(EmuControl.LEFT_STICK), display, guid, port)
        upsertStick(entries, keyIndex, playerIndex, "rstick", mapping.bindingFor(EmuControl.RIGHT_STICK), display, guid, port)
    }

    private fun upsertControl(
        entries: MutableList<LineEntry>,
        keyIndex: MutableMap<String, Int>,
        playerIndex: Int,
        keySuffix: String,
        binding: InputBinding?,
        display: String,
        guid: String,
        port: Int
    ) {
        val value = when (binding) {
            is InputBinding.Button -> buttonValue(
                button = binding.keyCode,
                display = display,
                guid = guid,
                port = port
            )

            is InputBinding.AxisDirection -> axisButtonValue(
                axis = binding.axis,
                invert = binding.sign.symbol,
                display = display,
                guid = guid,
                port = port
            )

            is InputBinding.Stick,
            null -> return
        }

        upsertLine(
            entries = entries,
            keyIndex = keyIndex,
            key = "player_${playerIndex}_$keySuffix",
            value = value
        )
    }

    private fun upsertStick(
        entries: MutableList<LineEntry>,
        keyIndex: MutableMap<String, Int>,
        playerIndex: Int,
        keySuffix: String,
        binding: InputBinding?,
        display: String,
        guid: String,
        port: Int
    ) {
        val stick = binding as? InputBinding.Stick ?: return

        upsertLine(
            entries = entries,
            keyIndex = keyIndex,
            key = "player_${playerIndex}_$keySuffix",
            value = stickValue(
                axisX = stick.axisX,
                axisY = stick.axisY,
                display = display,
                guid = guid,
                port = port
            )
        )
    }
}