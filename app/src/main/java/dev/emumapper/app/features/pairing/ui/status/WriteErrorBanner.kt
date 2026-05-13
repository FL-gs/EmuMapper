package dev.emumapper.app.features.pairing.ui.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.emumapper.app.core.ui.theme.AppTheme
import dev.emumapper.app.data.ini.WriteResult

private val WriteErrorRed = Color(0xFFFF545B)

@Composable
fun WriteErrorBanner(
    result: WriteResult?,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val message = result.toWriteErrorMessage()

    if (!visible || message == null) {
        return
    }

    Box(
        modifier = modifier
            .widthIn(max = 520.dp)
            .background(
                color = WriteErrorRed,
                shape = RoundedCornerShape(percent = 50)
            )
            .padding(
                start = 12.dp,
                end = 18.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.94f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = WriteErrorRed,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun WriteResult?.toWriteErrorMessage(): String? {
    return when (val result = this) {
        is WriteResult.Failure -> {
            "${result.emulatorId.toEmulatorDisplayName()}: ${result.reason}"
        }

        is WriteResult.PartialFailure -> {
            val messages = result.failures.map {
                "${it.emulatorId.toEmulatorDisplayName()}: ${it.reason}"
            }

            when {
                messages.size <= 2 -> {
                    messages.joinToString(", ")
                }

                else -> {
                    val firstTwo = messages.take(2).joinToString(", ")
                    val remaining = messages.size - 2
                    "$firstTwo + $remaining more"
                }
            }
        }

        else -> null
    }
}

private fun String.toEmulatorDisplayName(): String {
    return when (lowercase()) {
        "eden" -> "Eden"
        "dolphin" -> "Dolphin"
        "retroarch" -> "RetroArch"
        "duckstation" -> "DuckStation"
        "aethersx2" -> "AetherSX2"
        else -> replaceFirstChar { it.uppercase() }
    }
}

@Preview(
    name = "Write error banner",
    showBackground = true,
    widthDp = 900,
    heightDp = 180
)
@Composable
private fun WriteErrorBannerPreview() {
    AppTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            WriteErrorBanner(
                result = WriteResult.Failure(
                    emulatorId = "eden",
                    reason = "Config file not found"
                ),
                visible = true
            )
        }
    }
}