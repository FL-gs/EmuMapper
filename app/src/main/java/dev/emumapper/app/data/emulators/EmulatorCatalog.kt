package dev.emumapper.app.data.emulators

import androidx.annotation.DrawableRes
import dev.emumapper.app.R

/*
 * Add emulators detected by the app .
 */
data class EmulatorDef(
    val id: String,
    val label: String,
    val packageNames: List<String>,
    @DrawableRes val iconRes: Int
)

object EmulatorCatalog {
    val all: List<EmulatorDef> = listOf(
        EmulatorDef(
            id = "eden",
            label = "Eden",
            packageNames = listOf(
                EmulatorPackages.EDEN,
                EmulatorPackages.EDEN_NIGHTLY,
                EmulatorPackages.EDEN_LEGACY,
                EmulatorPackages.EDEN_LEGACY_NIGHTLY
            ),
            iconRes = R.drawable.ic_emu_eden
        ),
        EmulatorDef(
            id = "citron",
            label = "Citron",
            packageNames = listOf(EmulatorPackages.CITRON),
            iconRes = R.drawable.ic_emu_citron
        ),
        EmulatorDef(
            id = "dolphin",
            label = "Dolphin",
            packageNames = listOf(EmulatorPackages.DOLPHIN),
            iconRes = R.drawable.ic_emu_dolphin
        ),
        EmulatorDef(
            id = "retroarch",
            label = "RetroArch",
            packageNames = listOf(
                EmulatorPackages.RETROARCH_32,
                EmulatorPackages.RETROARCH_64
            ),
            iconRes = R.drawable.ic_emu_retroarch
        )

    )
}