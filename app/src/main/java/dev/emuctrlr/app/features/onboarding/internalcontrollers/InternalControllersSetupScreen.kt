package dev.emuctrlr.app.features.onboarding.internalcontrollers

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.emuctrlr.app.R
import dev.emuctrlr.app.core.input.PadKey
import dev.emuctrlr.app.core.input.internalControllerLabel
import dev.emuctrlr.app.core.input.mapKeyEvent
import dev.emuctrlr.app.core.ui.components.ActionButton
import dev.emuctrlr.app.features.components.DevicePickerViewModel
import dev.emuctrlr.app.features.components.SelectionDialog
import dev.emuctrlr.app.core.ui.components.AppConfirmDialog

@Composable
fun InternalControllersSetupScreen(
    initialInternalController: String?,
    onDone: (internalController: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val noneLabel = stringResource(R.string.none)
    val title = stringResource(R.string.internal_controllers_title)
    val nextLabel = stringResource(R.string.next)
    val explanationText = stringResource(R.string.internal_controllers_explanation)
    val explanationText2 = stringResource(R.string.internal_controllers_explanation2)
    val onboardinghint = stringResource(R.string.onboarding_internal_controller_settings_hint)

    val FOCUS_CONTROLLER = 0
    val FOCUS_NEXT = 1

    val rootFocusRequester = remember { FocusRequester() }
    val focusRequesters = remember { List(2) { FocusRequester() } }

    LaunchedEffect(Unit) {
        rootFocusRequester.requestFocus()
    }

    val pickerViewModel: DevicePickerViewModel = viewModel()
    val showDialog by pickerViewModel.showDialog.collectAsState()
    val choices by pickerViewModel.choices.collectAsState()

    var focusedIndex by rememberSaveable { mutableIntStateOf(FOCUS_CONTROLLER) }
    var internalController by rememberSaveable { mutableStateOf(initialInternalController) }

    var showSkipProfileDialog by rememberSaveable { mutableStateOf(false) }

    fun goNext() {
        if (internalController == null) {
            showSkipProfileDialog = true
        } else {
            onDone(internalController)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(rootFocusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (showDialog || showSkipProfileDialog) {
                    return@onPreviewKeyEvent false
                }

                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (mapKeyEvent(event.nativeKeyEvent)) {
                    PadKey.UP -> {
                        focusedIndex = (focusedIndex - 1).coerceAtLeast(FOCUS_CONTROLLER)
                        true
                    }

                    PadKey.DOWN -> {
                        focusedIndex = (focusedIndex + 1).coerceAtMost(FOCUS_NEXT)
                        true
                    }

                    PadKey.A -> {
                        when (focusedIndex) {
                            FOCUS_CONTROLLER -> {
                                pickerViewModel.open(noneLabel)
                                true
                            }

                            FOCUS_NEXT -> {
                                goNext()
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 88.dp
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = explanationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = explanationText2,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ActionButton(
                        text = internalControllerLabel(
                            internalController,
                            choices,
                            noneLabel
                        ),
                        selected = false,
                        active = true,
                        focusRequester = focusRequesters[FOCUS_CONTROLLER],
                        previousFocusRequester = null,
                        nextFocusRequester = focusRequesters[FOCUS_NEXT],
                        focused = focusedIndex == FOCUS_CONTROLLER,
                        onClick = {
                            focusedIndex = FOCUS_CONTROLLER
                            pickerViewModel.open(noneLabel)
                        }
                    )
                    Text(
                        text = onboardinghint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                }
            }
        }

        if (showDialog) {
            SelectionDialog(
                title = title,
                choices = choices,
                current = internalController,
                onPick = { picked ->
                    internalController = picked
                    pickerViewModel.close()
                },
                onDismiss = {
                    pickerViewModel.close()
                }
            )
        }

        if (showSkipProfileDialog) {
            AppConfirmDialog(
                title = stringResource(R.string.skip_internal_controller_title),
                message = stringResource(R.string.skip_internal_controller_message),
                confirmText = stringResource(R.string.continue_without_profile),
                dismissText = stringResource(R.string.cancel),
                onConfirm = {
                    showSkipProfileDialog = false
                    onDone(null)
                },
                onDismiss = {
                    showSkipProfileDialog = false
                }
            )
        }

        ActionButton(
            text = nextLabel,
            selected = focusedIndex == FOCUS_NEXT,
            active = true,
            focusRequester = focusRequesters[FOCUS_NEXT],
            focused = focusedIndex == FOCUS_NEXT,
            onClick = {
                goNext()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .widthIn(min = 120.dp, max = 160.dp)
        )
    }
}
