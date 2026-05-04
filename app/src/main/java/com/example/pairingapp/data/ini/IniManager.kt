package com.example.pairingapp.data.ini

import com.example.pairingapp.core.input.ControllerInfo
import com.example.pairingapp.core.utils.AppLogger
import com.example.pairingapp.core.utils.LogTags
import com.example.pairingapp.data.emulators.EmulatorCatalog
import com.example.pairingapp.data.ini.dolphin.DolphinRepository
import com.example.pairingapp.data.ini.eden.EdenRepository
import com.example.pairingapp.data.ini.retroarch.RetroArchRepository

object IniManager {

    fun writeAll(
        enabledEmulators: Set<String>,
        controllers: List<ControllerInfo>
    ): WriteResult {
        if (controllers.isEmpty()) {
            IniLog.skipNoControllers()
            return WriteResult.Success
        }

        IniLog.writeRequest(enabledEmulators, controllers)

        var successCount = 0
        val failures = mutableListOf<WriteResult.Failure>()

        EmulatorCatalog.all.forEach { emulator ->
            if (!enabledEmulators.contains(emulator.id)) {
                IniLog.emulatorDisabled(emulator.id)
                return@forEach
            }

            val result = when (emulator.id) {
                "dolphin" -> {
                    IniLog.emulatorWriting("dolphin")
                    DolphinRepository.writeControllers(controllers)
                }

                "eden" -> {
                    IniLog.emulatorWriting("eden")
                    EdenRepository.writeControllers(controllers)
                }

                "retroarch" -> {
                    IniLog.emulatorWriting("retroarch")
                    RetroArchRepository.configureRetroArch(
                        controllers = controllers
                    )
                }

                else -> {
                    IniLog.emulatorNotImplemented(emulator.id)
                    WriteResult.Success
                }
            }

            when (result) {
                is WriteResult.Success -> {
                    successCount++
                }

                is WriteResult.Failure -> {
                    failures += result
                    AppLogger.e(
                        LogTags.INI,
                        "emulator | ${result.emulatorId} | write failed | reason=${result.reason}",
                        result.throwable
                    )
                }

                is WriteResult.PartialFailure -> {
                    failures += result.failures
                    result.failures.forEach { failure ->
                        AppLogger.e(
                            LogTags.INI,
                            "emulator | ${failure.emulatorId} | write failed | reason=${failure.reason}",
                            failure.throwable
                        )
                    }
                }
            }
        }

        val finalResult = when {
            successCount > 0 && failures.isEmpty() -> {
                WriteResult.Success
            }

            successCount > 0 && failures.isNotEmpty() -> {
                WriteResult.PartialFailure(failures)
            }

            failures.size == 1 -> {
                failures.first()
            }

            failures.size > 1 -> {
                WriteResult.PartialFailure(failures)
            }

            else -> {
                WriteResult.Success
            }
        }

        AppLogger.d(
            LogTags.INI,
            when (finalResult) {
                is WriteResult.Success -> "writeAll result = SUCCESS"
                is WriteResult.Failure -> "writeAll result = FAIL (1)"
                is WriteResult.PartialFailure -> {
                    if (successCount > 0) {
                        "writeAll result = PARTIAL_SUCCESS | success=$successCount | fail=${finalResult.failures.size}"
                    } else {
                        "writeAll result = FAIL (${finalResult.failures.size})"
                    }
                }
            }
        )

        return finalResult
    }
}