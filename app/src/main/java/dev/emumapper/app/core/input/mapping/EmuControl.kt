package dev.emumapper.app.core.input.mapping

/*
 * Logical buttons/actions that EmuMapper can map.
 * Eden, Dolphin, RetroArch, etc. later translate these actions to their own naming scheme.
 */
enum class EmuControl(
    val stableKey: String,
    val displayName: String
) {
    A("a", "A"),
    B("b", "B"),
    X("x", "X"),
    Y("y", "Y"),

    START("start", "Start"),
    SELECT("select", "Select"),

    L1("l1", "L1"),
    R1("r1", "R1"),
    L2("l2", "L2"),
    R2("r2", "R2"),
    L3("l3", "L3"),
    R3("r3", "R3"),

    DPAD_UP("dpad_up", "D-Pad Up"),
    DPAD_DOWN("dpad_down", "D-Pad Down"),
    DPAD_LEFT("dpad_left", "D-Pad Left"),
    DPAD_RIGHT("dpad_right", "D-Pad Right"),

    LEFT_STICK("left_stick", "Left Stick"),
    RIGHT_STICK("right_stick", "Right Stick");

    companion object {
        val editableControls: List<EmuControl> = listOf(
            A,
            B,
            X,
            Y,
            START,
            SELECT,
            L1,
            R1,
            L2,
            R2,
            L3,
            R3,
            DPAD_UP,
            DPAD_DOWN,
            DPAD_LEFT,
            DPAD_RIGHT,
            LEFT_STICK,
            RIGHT_STICK
        )

        fun fromStableKey(value: String): EmuControl? {
            return entries.firstOrNull { it.stableKey == value }
        }
    }
}
