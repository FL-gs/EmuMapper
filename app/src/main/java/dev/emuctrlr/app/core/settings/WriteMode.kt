package dev.emuctrlr.app.core.settings

import androidx.annotation.StringRes
import dev.emuctrlr.app.R

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

