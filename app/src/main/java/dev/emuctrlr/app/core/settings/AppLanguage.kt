package dev.emuctrlr.app.core.settings

import androidx.annotation.StringRes
import dev.emuctrlr.app.R

enum class AppLanguage(@get:StringRes val labelRes: Int) {
    SYSTEM(R.string.language_system),
    FR(R.string.language_french),
    EN(R.string.language_english)
}

