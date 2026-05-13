package dev.emumapper.app.features.components

import androidx.lifecycle.ViewModel
import dev.emumapper.app.core.domain.controllers.ControllerScanner
import dev.emumapper.app.core.domain.controllers.DefaultControllerScanner
import dev.emumapper.app.core.input.ControllerInfo
import dev.emumapper.app.core.input.DeviceChoice
import dev.emumapper.app.core.input.internalProfileKey
import dev.emumapper.app.core.input.mapping.mappingProfileKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DevicePickerKeyMode {
    INTERNAL_PROFILE,
    MAPPING_PROFILE
}

class DevicePickerViewModel(
    private val controllerScanner: ControllerScanner = DefaultControllerScanner()
) : ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _controllers = MutableStateFlow<List<ControllerInfo>>(emptyList())
    val controllers: StateFlow<List<ControllerInfo>> = _controllers.asStateFlow()

    private val _choices = MutableStateFlow<List<DeviceChoice>>(emptyList())
    val choices: StateFlow<List<DeviceChoice>> = _choices.asStateFlow()

    fun open(
        firstChoiceLabel: String,
        keyMode: DevicePickerKeyMode = DevicePickerKeyMode.INTERNAL_PROFILE
    ) {
        _showDialog.value = true
        refresh(
            firstChoiceLabel = firstChoiceLabel,
            keyMode = keyMode
        )
    }

    fun close() {
        _showDialog.value = false
    }

    fun refresh(
        firstChoiceLabel: String,
        keyMode: DevicePickerKeyMode = DevicePickerKeyMode.INTERNAL_PROFILE
    ) {
        val scannedControllers = controllerScanner.scan()
            .distinctBy { controller -> controller.keyForMode(keyMode) }
            .sortedBy { it.name.lowercase() }

        _controllers.value = scannedControllers

        _choices.value = buildList {
            add(DeviceChoice(firstChoiceLabel, null))

            scannedControllers.forEach { controller ->
                add(
                    DeviceChoice(
                        label = controller.name,
                        key = controller.keyForMode(keyMode)
                    )
                )
            }
        }
    }

    private fun ControllerInfo.keyForMode(keyMode: DevicePickerKeyMode): String {
        return when (keyMode) {
            DevicePickerKeyMode.INTERNAL_PROFILE -> internalProfileKey()
            DevicePickerKeyMode.MAPPING_PROFILE -> mappingProfileKey()
        }
    }
}
