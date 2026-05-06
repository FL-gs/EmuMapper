package dev.emuctrlr.app.data.emulators

import androidx.annotation.DrawableRes
import dev.emuctrlr.app.R

/**
 * ajout des emulateurs detecté par l'app ici.
 * Pour connaitre le package je peux utiliser Package Name Viewer 2.0
 * Penser a modifier aussi l'androidManifest pour chaque ajout
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
            packageNames = listOf(EmulatorPackages.EDEN),
            iconRes = R.drawable.ic_emu_eden
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