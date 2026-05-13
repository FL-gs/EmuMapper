package dev.emumapper.app.features.settings.mapping

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.emumapper.app.R
import dev.emumapper.app.core.input.ControllerInfo
import dev.emumapper.app.core.input.mapping.ControllerMapping
import dev.emumapper.app.core.input.mapping.ControllerMappingResolver
import dev.emumapper.app.core.input.mapping.EmuControl
import dev.emumapper.app.core.input.mapping.InputBinding
import dev.emumapper.app.core.input.mapping.MappingProfiles
import dev.emumapper.app.core.input.mapping.mappingProfileKey
import dev.emumapper.app.core.ui.components.ActionButton
import dev.emumapper.app.core.ui.components.AppDialog
import dev.emumapper.app.core.ui.components.AppSettingsFocusZone
import dev.emumapper.app.core.ui.components.HintBarState
import dev.emumapper.app.core.ui.components.SectionDivider
import dev.emumapper.app.features.components.DevicePickerKeyMode
import dev.emumapper.app.features.components.DevicePickerViewModel
import dev.emumapper.app.features.components.SelectionDialog
import kotlinx.coroutines.delay

private const val BUTTON_CAPTURE_TIMEOUT_MS = 3_000L
private const val BUTTON_CAPTURE_TICK_MS = 100L

private data class CaptureRequest(
    val controllerName: String,
    val control: EmuControl
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun MappingScreen(
    active: Boolean,
    visibleControllers: List<ControllerInfo>,
    controllerMappingOverrides: Map<String, ControllerMapping>,
    onSetControllerMappingBinding: (String, EmuControl, InputBinding?) -> Unit,
    onResetControllerMapping: (String) -> Unit,
    onHintStateChanged: (HintBarState) -> Unit,
    modifier: Modifier = Modifier
) {
    val resolver = remember { ControllerMappingResolver() }
    val pickerViewModel: DevicePickerViewModel = viewModel()

    val showControllerDialog by pickerViewModel.showDialog.collectAsState()
    val choices by pickerViewModel.choices.collectAsState()
    val controllers by pickerViewModel.controllers.collectAsState()

    val controllerFocusRequester = remember { FocusRequester() }
    val resetFocusRequester = remember { FocusRequester() }
    val controls = remember {
        EmuControl.editableControls.filterNot { control ->
            control == EmuControl.LEFT_STICK ||
                    control == EmuControl.RIGHT_STICK ||
                    control == EmuControl.DPAD_UP ||
                    control == EmuControl.DPAD_DOWN ||
                    control == EmuControl.DPAD_LEFT ||
                    control == EmuControl.DPAD_RIGHT
        }
    }
    val changeFocusRequesters = remember {
        controls.map { FocusRequester() }
    }

    val previewAlpha = if (active) 1f else 0.35f
    val defaultLabel = stringResource(R.string.mapping_profile_default)

    var selectedControllerKey by rememberSaveable { mutableStateOf<String?>(null) }
    var captureRequest by remember { mutableStateOf<CaptureRequest?>(null) }
    var focusAfterDialogIndex by remember { mutableStateOf<Int?>(null) }
    var focusControllerAfterReset by remember { mutableStateOf(false) }

    LaunchedEffect(defaultLabel) {
        pickerViewModel.refresh(
            firstChoiceLabel = defaultLabel,
            keyMode = DevicePickerKeyMode.MAPPING_PROFILE
        )
    }

    LaunchedEffect(focusControllerAfterReset) {
        if (focusControllerAfterReset) {
            delay(80)
            focusControllerAfterReset = false
            controllerFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(active, showControllerDialog, captureRequest) {
        if (active && !showControllerDialog && captureRequest == null) {
            val rowIndexToRestore = focusAfterDialogIndex
            focusAfterDialogIndex = null

            delay(50)

            if (rowIndexToRestore != null) {
                changeFocusRequesters.getOrNull(rowIndexToRestore)?.requestFocus()
            } else {
                controllerFocusRequester.requestFocus()
            }

            onHintStateChanged(
                HintBarState.AppSettings(AppSettingsFocusZone.CONTROLLER_BUTTON)
            )
        }
    }

    LaunchedEffect(controllers, selectedControllerKey) {
        if (
            selectedControllerKey != null &&
            controllers.none { it.mappingProfileKey() == selectedControllerKey }
        ) {
            selectedControllerKey = null
        }
    }

    val selectedController = selectedControllerKey?.let { key ->
        controllers.firstOrNull { it.mappingProfileKey() == key }
    }

    val selectedControllerMappingKey = selectedController?.mappingProfileKey()
    val hasCustomMapping = selectedControllerMappingKey != null &&
            controllerMappingOverrides.containsKey(selectedControllerMappingKey)

    val mapping = selectedController?.let { controller ->
        resolver.resolve(
            controller = controller,
            userOverridesByName = controllerMappingOverrides
        )
    } ?: MappingProfiles.androidStandard

    val selectedControllerLabel = selectedController?.name ?: defaultLabel
    val mappingEditable = active && selectedController != null
    val resetActive = active && selectedController != null && hasCustomMapping

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 86.dp, bottom = 36.dp)
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .graphicsLayer { alpha = previewAlpha }
            ) {
                Text(
                    text = stringResource(R.string.mapping_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.mapping_subtitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )

                Spacer(Modifier.height(10.dp))

                ActionButton(
                    text = selectedControllerLabel,
                    selected = false,
                    active = active,
                    focusRequester = controllerFocusRequester,
                    nextFocusRequester = when {
                        resetActive -> resetFocusRequester
                        mappingEditable -> changeFocusRequesters.firstOrNull()
                        else -> null
                    },
                    onFocused = {
                        onHintStateChanged(
                            HintBarState.AppSettings(AppSettingsFocusZone.CONTROLLER_BUTTON)
                        )
                    },
                    onClick = {
                        pickerViewModel.open(
                            firstChoiceLabel = defaultLabel,
                            keyMode = DevicePickerKeyMode.MAPPING_PROFILE
                        )
                    }
                )

                if (selectedController != null) {
                    Spacer(Modifier.height(6.dp))

                    MappingResetButton(
                        text = stringResource(R.string.mapping_reset_layout),
                        active = resetActive,
                        focusRequester = resetFocusRequester,
                        previousFocusRequester = controllerFocusRequester,
                        nextFocusRequester = changeFocusRequesters.firstOrNull(),
                        onFocused = {
                            onHintStateChanged(
                                HintBarState.AppSettings(AppSettingsFocusZone.CONTROLLER_BUTTON)
                            )
                        },
                        onClick = {
                            val controller = selectedController ?: return@MappingResetButton

                            onResetControllerMapping(controller.name)
                            focusControllerAfterReset = true
                        }
                    )
                }

                SectionDivider()

                if (selectedController == null) {
                    SelectControllerFirstState()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        controls.forEachIndexed { index, control ->
                            MappingBindingRow(
                                controlLabel = control.displayName,
                                bindingLabel = mapping.bindingFor(control)?.displayLabel()
                                    ?: stringResource(R.string.mapping_not_mapped),
                                actionLabel = stringResource(R.string.mapping_change),
                                active = mappingEditable,
                                focusRequester = changeFocusRequesters[index],
                                previousFocusRequester = if (index == 0) {
                                    if (resetActive) resetFocusRequester else controllerFocusRequester
                                } else {
                                    changeFocusRequesters[index - 1]
                                },
                                nextFocusRequester = changeFocusRequesters.getOrNull(index + 1),
                                onFocused = {
                                    onHintStateChanged(
                                        HintBarState.AppSettings(AppSettingsFocusZone.CONTROLLER_BUTTON)
                                    )
                                },
                                onClick = {
                                    val controller = selectedController ?: return@MappingBindingRow

                                    focusAfterDialogIndex = index
                                    captureRequest = CaptureRequest(
                                        controllerName = controller.name,
                                        control = control
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showControllerDialog) {
        SelectionDialog(
            title = stringResource(R.string.mapping_controller_dialog_title),
            choices = choices,
            current = selectedControllerKey,
            onPick = { picked ->
                selectedControllerKey = picked
                pickerViewModel.close()
            },
            onDismiss = {
                pickerViewModel.close()
            }
        )
    }

    captureRequest?.let { request ->
        ButtonCaptureDialog(
            controllerName = request.controllerName,
            control = request.control,
            timeoutMs = BUTTON_CAPTURE_TIMEOUT_MS,
            onDismiss = {
                captureRequest = null
            },
            onTimeout = {
                captureRequest = null
            },
            onCaptured = { binding ->
                onSetControllerMappingBinding(
                    request.controllerName,
                    request.control,
                    binding
                )
                captureRequest = null
            }
        )
    }
}

@Composable
private fun SelectControllerFirstState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.mapping_select_controller_first_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.mapping_select_controller_first_subtitle),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MappingResetButton(
    text: String,
    active: Boolean,
    focusRequester: FocusRequester,
    previousFocusRequester: FocusRequester?,
    nextFocusRequester: FocusRequester?,
    onFocused: () -> Unit,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val textColor = when {
        active && isFocused -> MaterialTheme.colorScheme.primary
        active -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
    }

    val fontWeight = if (active && isFocused) {
        FontWeight.SemiBold
    } else {
        FontWeight.Normal
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocused()
            }
            .focusRequester(focusRequester)
            .focusProperties {
                previousFocusRequester?.let { up = it }
                nextFocusRequester?.let { down = it }
            }
            .onPreviewKeyEvent { event ->
                if (!active) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.key) {
                    Key.Enter,
                    Key.DirectionCenter,
                    Key.NumPadEnter,
                    Key.ButtonA -> {
                        onClick()
                        true
                    }

                    else -> false
                }
            }
            .focusable(enabled = active)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun MappingBindingRow(
    controlLabel: String,
    bindingLabel: String,
    actionLabel: String,
    active: Boolean,
    focusRequester: FocusRequester,
    previousFocusRequester: FocusRequester?,
    nextFocusRequester: FocusRequester?,
    onFocused: () -> Unit,
    onClick: () -> Unit
) {
    var rowFocused by remember { mutableStateOf(false) }

    MappingBaseRow(
        focused = active && rowFocused
    ) {
        Text(
            text = controlLabel,
            modifier = Modifier.weight(0.9f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = bindingLabel,
            modifier = Modifier.weight(1.2f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
            maxLines = 1
        )

        MappingChangeButton(
            text = actionLabel,
            active = active,
            focusRequester = focusRequester,
            previousFocusRequester = previousFocusRequester,
            nextFocusRequester = nextFocusRequester,
            onFocusChanged = { focused ->
                rowFocused = focused

                if (focused) {
                    onFocused()
                }
            },
            onClick = onClick
        )
    }
}

@Composable
private fun MappingChangeButton(
    text: String,
    active: Boolean,
    focusRequester: FocusRequester,
    previousFocusRequester: FocusRequester?,
    nextFocusRequester: FocusRequester?,
    onFocusChanged: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val textColor = if (active && isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    val fontWeight = if (active && isFocused) {
        FontWeight.SemiBold
    } else {
        FontWeight.Normal
    }

    Box(
        modifier = Modifier
            .width(112.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChanged(it.isFocused)
            }
            .focusRequester(focusRequester)
            .focusProperties {
                previousFocusRequester?.let { up = it }
                nextFocusRequester?.let { down = it }
            }
            .onPreviewKeyEvent { event ->
                if (!active) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.key) {
                    Key.Enter,
                    Key.DirectionCenter,
                    Key.NumPadEnter,
                    Key.ButtonA -> {
                        onClick()
                        true
                    }

                    else -> false
                }
            }
            .focusable(enabled = active)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun ButtonCaptureDialog(
    controllerName: String,
    control: EmuControl,
    timeoutMs: Long,
    onDismiss: () -> Unit,
    onTimeout: () -> Unit,
    onCaptured: (InputBinding.Button) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var remainingMs by remember(control) { mutableLongStateOf(timeoutMs) }

    val captureMessage = stringResource(
        R.string.mapping_capture_message,
        control.displayName
    )

    LaunchedEffect(control) {
        delay(50)
        focusRequester.requestFocus()

        val startMs = SystemClock.elapsedRealtime()

        while (true) {
            val elapsedMs = SystemClock.elapsedRealtime() - startMs
            val nextRemainingMs = (timeoutMs - elapsedMs).coerceAtLeast(0L)
            remainingMs = nextRemainingMs

            if (nextRemainingMs <= 0L) break

            delay(BUTTON_CAPTURE_TICK_MS)
        }

        onTimeout()
    }

    AppDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = controllerName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) {
                            return@onPreviewKeyEvent true
                        }

                        val keyCode = event.nativeKeyEvent.keyCode

                        onCaptured(
                            InputBinding.Button(keyCode = keyCode)
                        )

                        true
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = captureMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = ((remainingMs + 999L) / 1_000L).toString(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
                    )
                }
            }
        }
    )
}

@Composable
private fun MappingBaseRow(
    focused: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val bottomBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)

    val backgroundColor = if (focused) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2f

                drawLine(
                    color = bottomBorderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}
