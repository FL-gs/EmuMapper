package dev.emumapper.app.features.update

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.emumapper.app.core.input.PadKey
import dev.emumapper.app.core.input.mapKeyEvent
import dev.emumapper.app.core.ui.components.AppDialog
import dev.emumapper.app.core.update.AppUpdateInfo
import kotlinx.coroutines.android.awaitFrame
import dev.emumapper.app.R

private const val UPDATE_ACTION_SKIP_VERSION = 0
private const val UPDATE_ACTION_CANCEL = 1
private const val UPDATE_ACTION_OK = 2

@Composable
fun UpdateAvailableDialog(
    update: AppUpdateInfo,
    currentVersionName: String,
    onOpenRelease: () -> Unit,
    onSkipVersion: () -> Unit,
    onDismiss: () -> Unit
) {
    var focusedAction by rememberSaveable {
        mutableIntStateOf(UPDATE_ACTION_CANCEL)
    }

    val focusRequester = remember { FocusRequester() }

    fun runFocusedAction() {
        when (focusedAction) {
            UPDATE_ACTION_SKIP_VERSION -> onSkipVersion()
            UPDATE_ACTION_OK -> onOpenRelease()
            else -> onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    AppDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (mapKeyEvent(event.nativeKeyEvent)) {
                    PadKey.LEFT -> {
                        focusedAction = (focusedAction - 1)
                            .coerceAtLeast(UPDATE_ACTION_SKIP_VERSION)
                        true
                    }

                    PadKey.RIGHT -> {
                        focusedAction = (focusedAction + 1)
                            .coerceAtMost(UPDATE_ACTION_OK)
                        true
                    }

                    PadKey.A -> {
                        runFocusedAction()
                        true
                    }

                    PadKey.B -> {
                        onDismiss()
                        true
                    }

                    else -> false
                }
            },
        title = {
            Text(
                text = stringResource(R.string.update_available_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = stringResource(
                    R.string.update_available_message,
                    update.versionName,
                    currentVersionName
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Row {
                UpdateDialogButton(
                    text = stringResource(R.string.update_skip_this_version),
                    focused = focusedAction == UPDATE_ACTION_SKIP_VERSION,
                    onClick = onSkipVersion
                )

                Spacer(Modifier.width(8.dp))

                UpdateDialogButton(
                    text = stringResource(R.string.update_cancel),
                    focused = focusedAction == UPDATE_ACTION_CANCEL,
                    onClick = onDismiss
                )

                Spacer(Modifier.width(8.dp))

                UpdateDialogButton(
                    text = stringResource(R.string.update_ok),
                    focused = focusedAction == UPDATE_ACTION_OK,
                    onClick = onDismiss
                )
            }
        }
    )
}

@Composable
private fun UpdateDialogButton(
    text: String,
    focused: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (focused) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}