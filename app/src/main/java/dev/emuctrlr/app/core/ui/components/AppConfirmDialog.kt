package dev.emuctrlr.app.core.ui.components

import androidx.compose.foundation.focusable
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
import androidx.compose.ui.unit.dp
import dev.emuctrlr.app.core.input.PadKey
import dev.emuctrlr.app.core.input.mapKeyEvent
import kotlinx.coroutines.android.awaitFrame

private const val DIALOG_ACTION_DISMISS = 0
private const val DIALOG_ACTION_CONFIRM = 1

@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var focusedAction by rememberSaveable {
        mutableIntStateOf(DIALOG_ACTION_DISMISS)
    }

    val focusRequester = remember { FocusRequester() }

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
                        focusedAction = DIALOG_ACTION_DISMISS
                        true
                    }

                    PadKey.RIGHT -> {
                        focusedAction = DIALOG_ACTION_CONFIRM
                        true
                    }

                    PadKey.A -> {
                        if (focusedAction == DIALOG_ACTION_CONFIRM) {
                            onConfirm()
                        } else {
                            onDismiss()
                        }
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (focusedAction == DIALOG_ACTION_DISMISS) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            ) {
                Text(dismissText)
            }

            Spacer(Modifier.width(8.dp))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (focusedAction == DIALOG_ACTION_CONFIRM) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            ) {
                Text(confirmText)
            }
        }
    )
}