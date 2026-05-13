package dev.emumapper.app.core.input.mapping

/**
 * Direction d'un axe Android.
 *
 * Exemple :
 * - AXIS_HAT_X + NEGATIVE = D-Pad Left
 * - AXIS_HAT_X + POSITIVE = D-Pad Right
 */
enum class AxisSign(
    val symbol: String
) {
    NEGATIVE("-"),
    POSITIVE("+");

    companion object {
        fun fromSymbol(value: String): AxisSign? {
            return entries.firstOrNull { it.symbol == value }
        }
    }
}
