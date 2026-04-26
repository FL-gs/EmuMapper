package com.example.pairingapp.core.pairing

import com.example.pairingapp.core.domain.controllers.ControllerType
import com.example.pairingapp.core.domain.controllers.DetectedController
import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.input.toLogLine
import com.example.pairingapp.core.input.uniqueKey
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags

class DefaultVisibleControllersResolver : VisibleControllersResolver {

    override fun resolve(
        controllers: List<DetectedController>
    ): List<ControllerInfo> {

        val internalControllers = mutableListOf<ControllerInfo>()
        val externalControllers = mutableListOf<ControllerInfo>()

        controllers.forEach { controller ->
            val isInternal = controller.type == ControllerType.INTERNAL
            val info = controller.info

            val target = if (isInternal) internalControllers else externalControllers
            val type = if (isInternal) "internal" else "external"
            val line = info.toLogLine()

            if (target.any { it.uniqueKey() == info.uniqueKey() }) {
                AppLogger.d(LogTags.PAIRING, "pairing skip | duplicate $type | $line")
                return@forEach
            }

            if (isInternal && externalControllers.isNotEmpty()) {
                AppLogger.d(
                    LogTags.PAIRING,
                    "pairing skip | internal blocked by active external | $line"
                )
                return@forEach
            }

            if (isInternal && internalControllers.isNotEmpty()) {
                AppLogger.d(LogTags.PAIRING, "pairing skip | internal already assigned | $line")
                return@forEach
            }

            if (!isInternal && externalControllers.size >= 4) {
                AppLogger.d(LogTags.PAIRING, "pairing skip | external limit reached | $line")
                return@forEach
            }

            target += info
            AppLogger.d(LogTags.PAIRING, "pairing add | $type | $line")
        }

        return if (externalControllers.isNotEmpty()) {
            externalControllers.take(4)
        } else {
            internalControllers.take(1)
        }
    }
}