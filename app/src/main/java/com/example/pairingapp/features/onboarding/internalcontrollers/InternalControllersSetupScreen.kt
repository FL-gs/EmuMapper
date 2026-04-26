package com.example.pairingapp.features.onboarding.internalcontrollers

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pairingapp.R
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.availableInternalControllerChoices
import com.example.pairingapp.core.input.internalControllerLabel
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.ui.components.ActionButton
import com.example.pairingapp.core.ui.theme.AppTheme
import com.example.pairingapp.features.components.DevicePickerViewModel
import com.example.pairingapp.features.components.SelectionDialog

@Composable
fun InternalControllersSetupScreen(
    initialInternal1: String?,
    initialInternal2: String?,
    onDone: (internal1: String?, internal2: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val noneLabel = stringResource(R.string.none)
    val title = stringResource(R.string.internal_controllers_title)
    val internalController1Label = stringResource(R.string.internal_controller_1)
    val internalController2Label = stringResource(R.string.internal_controller_2)
    val nextLabel = stringResource(R.string.next)

    val explanationText = stringResource(R.string.internal_controllers_explanation)

    val FOCUS_SLOT_1 = 0
    val FOCUS_SLOT_2 = 1
    val FOCUS_NEXT = 2

    val rootFocusRequester = remember { FocusRequester() }
    val focusRequesters = remember { List(3) { FocusRequester() } }

    LaunchedEffect(Unit) {
        rootFocusRequester.requestFocus()
    }

    val pickerViewModel: DevicePickerViewModel = viewModel()
    val showDialog by pickerViewModel.showDialog.collectAsState()
    val choices by pickerViewModel.choices.collectAsState()

    var focusedIndex by rememberSaveable { mutableIntStateOf(FOCUS_SLOT_1) }
    var internal1 by rememberSaveable { mutableStateOf(initialInternal1) }
    var internal2 by rememberSaveable { mutableStateOf(initialInternal2) }

    val selectedSlot = when (focusedIndex) {
        FOCUS_SLOT_2 -> 1
        else -> 0
    }

    val dialogChoices = availableInternalControllerChoices(
        choices = choices,
        selectedSlot = selectedSlot,
        internal1 = internal1,
        internal2 = internal2
    )

    fun setSlotValue(value: String?) {
        when (focusedIndex) {
            FOCUS_SLOT_1 -> internal1 = value
            FOCUS_SLOT_2 -> internal2 = value
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(rootFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (showDialog) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (mapKeyEvent(event.nativeKeyEvent)) {
                    PadKey.UP -> {
                        focusedIndex = (focusedIndex - 1).coerceAtLeast(FOCUS_SLOT_1)
                        true
                    }

                    PadKey.DOWN -> {
                        focusedIndex = (focusedIndex + 1).coerceAtMost(FOCUS_NEXT)
                        true
                    }

                    PadKey.A -> {
                        when (focusedIndex) {
                            FOCUS_SLOT_1, FOCUS_SLOT_2 -> {
                                pickerViewModel.open(noneLabel)
                                true
                            }

                            FOCUS_NEXT -> {
                                onDone(internal1, internal2)
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val isCompact = maxWidth.value < 900f
            val horizontalPadding = if (isCompact) 24.dp else 64.dp
            val verticalPadding = if (isCompact) 24.dp else 48.dp

            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(
                        horizontal = horizontalPadding,
                        vertical = verticalPadding
                    )
            ) {
                if (isCompact) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 88.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = explanationText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 420.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActionButton(
                                text = internalControllerLabel(
                                    internal1,
                                    choices,
                                    noneLabel
                                ),
                                selected = false,
                                active = true,
                                focusRequester = focusRequesters[0],
                                previousFocusRequester = null,
                                nextFocusRequester = focusRequesters[1],
                                focused = focusedIndex == FOCUS_SLOT_1,
                                onClick = {
                                    focusedIndex = FOCUS_SLOT_1
                                    pickerViewModel.open(noneLabel)
                                }
                            )

                            ActionButton(
                                text = internalControllerLabel(
                                    internal2,
                                    choices,
                                    noneLabel
                                ),
                                selected = false,
                                active = true,
                                focusRequester = focusRequesters[1],
                                previousFocusRequester = focusRequesters[0],
                                nextFocusRequester = focusRequesters[2],
                                focused = focusedIndex == FOCUS_SLOT_2,
                                onClick = {
                                    focusedIndex = FOCUS_SLOT_2
                                    pickerViewModel.open(noneLabel)
                                }
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(89.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = explanationText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }

                        Column(
                            modifier = Modifier.width(300.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActionButton(
                                text = internalControllerLabel(
                                    internal1,
                                    choices,
                                    noneLabel
                                ),
                                selected = false,
                                active = true,
                                focusRequester = focusRequesters[0],
                                previousFocusRequester = null,
                                nextFocusRequester = focusRequesters[1],
                                focused = focusedIndex == FOCUS_SLOT_1,
                                onClick = {
                                    focusedIndex = FOCUS_SLOT_1
                                    pickerViewModel.open(noneLabel)
                                }
                            )

                            ActionButton(
                                text = internalControllerLabel(
                                    internal2,
                                    choices,
                                    noneLabel
                                ),
                                selected = false,
                                active = true,
                                focusRequester = focusRequesters[1],
                                previousFocusRequester = focusRequesters[0],
                                nextFocusRequester = focusRequesters[2],
                                focused = focusedIndex == FOCUS_SLOT_2,
                                onClick = {
                                    focusedIndex = FOCUS_SLOT_2
                                    pickerViewModel.open(noneLabel)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            SelectionDialog(
                title = when (focusedIndex) {
                    FOCUS_SLOT_1 -> internalController1Label
                    FOCUS_SLOT_2 -> internalController2Label
                    else -> internalController1Label
                },
                choices = dialogChoices,
                current = when (focusedIndex) {
                    FOCUS_SLOT_1 -> internal1
                    FOCUS_SLOT_2 -> internal2
                    else -> internal1
                },
                onPick = { picked ->
                    setSlotValue(picked)
                    pickerViewModel.close()
                },
                onDismiss = {
                    pickerViewModel.close()
                }
            )
        }

        ActionButton(
            text = nextLabel,
            selected = focusedIndex == FOCUS_NEXT,
            active = true,
            focusRequester = focusRequesters[2],
            focused = focusedIndex == FOCUS_NEXT,
            onClick = {
                onDone(internal1, internal2)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .widthIn(min = 120.dp, max = 160.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 720)
@Composable
fun InternalControllersSetupScreenPreview() {
    AppTheme(darkTheme = false) {
        InternalControllersSetupScreen(
            initialInternal1 = "odin2 portal",
            initialInternal2 = null,
            onDone = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 900)
@Composable
fun InternalControllersSetupScreenPreviewCompact() {
    AppTheme(darkTheme = false) {
        InternalControllersSetupScreen(
            initialInternal1 = "odin2 portal",
            initialInternal2 = null,
            onDone = { _, _ -> }
        )
    }
}