package com.example.pairingapp.core.settings

import androidx.annotation.StringRes
import com.example.pairingapp.R

enum class WriteMode(
    @get:StringRes val labelRes: Int,
    @get:StringRes val descriptionRes: Int
) {
    MANUAL(
        R.string.write_mode_manual,
        R.string.write_mode_desc_manual
    ),
    AUTO(
        R.string.write_mode_automatic,
        R.string.write_mode_desc_auto
    )
}

