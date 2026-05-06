package dev.emuctrlr.app.features.components

import androidx.lifecycle.ViewModel
import dev.emuctrlr.app.core.domain.controllers.ControllerScanner
import dev.emuctrlr.app.core.domain.controllers.DefaultControllerScanner
import dev.emuctrlr.app.core.input.DeviceChoice
import dev.emuctrlr.app.core.input.internalProfileKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DevicePickerViewModel(
    private val controllerScanner: ControllerScanner = DefaultControllerScanner()
) : ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _choices = MutableStateFlow<List<DeviceChoice>>(emptyList())
    val choices: StateFlow<List<DeviceChoice>> = _choices.asStateFlow()

    fun open(noneLabel: String) {
        _showDialog.value = true
        refresh(noneLabel)
    }

    fun close() {
        _showDialog.value = false
    }

    fun refresh(noneLabel: String) {
        val controllers = controllerScanner.scan()
            .distinctBy { it.internalProfileKey() }
            .sortedBy { it.name.lowercase() }

        _choices.value = buildList {
            add(DeviceChoice(noneLabel, null))
            controllers.forEach { controller ->
                add(
                    DeviceChoice(
                        label = controller.name,
                        key = controller.internalProfileKey()
                    )
                )
            }
        }
    }
}