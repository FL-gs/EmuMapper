package dev.emumapper.app.core.input.mapping

data class ControllerMapping(
    val bindings: Map<EmuControl, InputBinding>
) {
    fun bindingFor(control: EmuControl): InputBinding? {
        return bindings[control]
    }

    fun withBinding(
        control: EmuControl,
        binding: InputBinding
    ): ControllerMapping {
        return copy(
            bindings = bindings.toMutableMap().apply {
                this[control] = binding
            }
        )
    }

    fun withoutBinding(control: EmuControl): ControllerMapping {
        return copy(
            bindings = bindings.toMutableMap().apply {
                remove(control)
            }
        )
    }

    /*
     * Applies user overrides on top of a default mapping.
     */
    fun mergedWithOverrides(overrides: ControllerMapping?): ControllerMapping {
        if (overrides == null || overrides.bindings.isEmpty()) return this

        return ControllerMapping(
            bindings = bindings + overrides.bindings
        )
    }

    /*
     * Stable hash used to include controller mappings in WriteSnapshot.
     */
    fun stableHash(): String {
        return bindings
            .toSortedMap(compareBy { it.stableKey })
            .entries
            .joinToString(separator = "|") { (control, binding) ->
                "${control.stableKey}=${binding.stableString()}"
            }
    }
}

private fun InputBinding.stableString(): String {
    return when (this) {
        is InputBinding.Button ->
            "button:$keyCode"

        is InputBinding.AxisDirection ->
            "axis_direction:$axis:${sign.symbol}"

        is InputBinding.Stick ->
            "stick:$axisX:$axisY"
    }
}
