package com.example.pairingapp.features.pairing.ui.status

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object WriteStatusDefaults {
    val SuccessColor = Color(0xFF16A34A)

    const val ProgressTrackAlpha = 0.18f
    const val ProgressGradientRotationDurationMillis = 2000

    const val CircleDelayMillis = 120L

    val CircleSize = 36.dp
    val CircleStrokeWidth = 2.dp

    val CheckSize = 20.dp
    val CheckStrokeWidth = 2.5.dp

    const val TextAlphaDurationMillis = 220
    const val TextScaleOutDurationMillis = 240
    const val TextScaleInDurationMillis = 180

    const val CircleAlphaDurationMillis = 220
    const val CircleScaleInDurationMillis = 260
    const val CircleScaleOutDurationMillis = 120
    const val CircleColorDurationMillis = 180

    const val CheckAlphaDurationMillis = 120
    const val CheckScaleUpDurationMillis = 140
    const val CheckScaleDownDurationMillis = 180
    const val CheckDrawDurationMillis = 420

    val StatusHeight = 64.dp
}