package dev.emuctrlr.app.core.input.mapping

/**
 * Mapping complet ou partiel d'une manette.
 *
 * Pour les profils par défaut : remplit normalement toutes les actions connues.
 * Pour les overrides utilisateur : stocker que les actions modifiées.
 */
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

    /**
     * Applique des overrides utilisateur par-dessus un mapping par défaut.
     */
    fun mergedWithOverrides(overrides: ControllerMapping?): ControllerMapping {
        if (overrides == null || overrides.bindings.isEmpty()) return this

        return ControllerMapping(
            bindings = bindings + overrides.bindings
        )
    }

    /**
     * Hash stable pour inclure le mapping dans WriteSnapshot plus tard.
     *
     * Sans ça, l'app peut croire que la config est déjà écrite alors que seul le mapping
     * a changé.
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
