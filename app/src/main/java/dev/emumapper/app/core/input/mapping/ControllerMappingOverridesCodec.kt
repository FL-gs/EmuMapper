package dev.emumapper.app.core.input.mapping

import org.json.JSONObject

/*
 * Encodes and decodes user controller mapping overrides stored in DataStore.
 */
object ControllerMappingOverridesCodec {

    fun encode(overridesByName: Map<String, ControllerMapping>): String {
        val root = JSONObject()

        overridesByName
            .mapKeys { (name, _) -> name.toMappingProfileKey() }
            .filterValues { it.bindings.isNotEmpty() }
            .toSortedMap()
            .forEach { (controllerKey, mapping) ->
                val mappingObject = JSONObject()

                mapping.bindings
                    .toSortedMap(compareBy { it.stableKey })
                    .forEach { (control, binding) ->
                        mappingObject.put(control.stableKey, binding.toJsonObject())
                    }

                root.put(controllerKey, mappingObject)
            }

        return root.toString()
    }

    fun decode(json: String?): Map<String, ControllerMapping> {
        if (json.isNullOrBlank()) return emptyMap()

        return runCatching {
            val root = JSONObject(json)
            val result = linkedMapOf<String, ControllerMapping>()
            val controllerKeys = root.keys()

            while (controllerKeys.hasNext()) {
                val rawControllerKey = controllerKeys.next()
                val controllerKey = rawControllerKey.toMappingProfileKey()
                val mappingObject = root.optJSONObject(rawControllerKey) ?: continue
                val bindings = linkedMapOf<EmuControl, InputBinding>()
                val controlKeys = mappingObject.keys()

                while (controlKeys.hasNext()) {
                    val controlKey = controlKeys.next()
                    val control = EmuControl.fromStableKey(controlKey) ?: continue
                    val bindingObject = mappingObject.optJSONObject(controlKey) ?: continue
                    val binding = bindingObject.toInputBindingOrNull() ?: continue
                    bindings[control] = binding
                }

                if (bindings.isNotEmpty()) {
                    result[controllerKey] = ControllerMapping(bindings)
                }
            }

            result.toMap()
        }.getOrElse {
            emptyMap()
        }
    }
}

private fun InputBinding.toJsonObject(): JSONObject {
    val obj = JSONObject()

    when (this) {
        is InputBinding.Button -> {
            obj.put("type", "button")
            obj.put("keyCode", keyCode)
        }

        is InputBinding.AxisDirection -> {
            obj.put("type", "axis_direction")
            obj.put("axis", axis)
            obj.put("sign", sign.symbol)
        }

        is InputBinding.Stick -> {
            obj.put("type", "stick")
            obj.put("axisX", axisX)
            obj.put("axisY", axisY)
        }
    }

    return obj
}

private fun JSONObject.toInputBindingOrNull(): InputBinding? {
    return when (optString("type")) {
        "button" -> {
            if (!has("keyCode")) return null
            InputBinding.Button(keyCode = optInt("keyCode"))
        }

        "axis_direction" -> {
            if (!has("axis") || !has("sign")) return null

            val sign = AxisSign.fromSymbol(optString("sign")) ?: return null

            InputBinding.AxisDirection(
                axis = optInt("axis"),
                sign = sign
            )
        }

        "stick" -> {
            if (!has("axisX") || !has("axisY")) return null

            InputBinding.Stick(
                axisX = optInt("axisX"),
                axisY = optInt("axisY")
            )
        }

        else -> null
    }
}
