package dev.emumapper.app.core.input.mapping

/*
 * Direction of an Android axis.
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
