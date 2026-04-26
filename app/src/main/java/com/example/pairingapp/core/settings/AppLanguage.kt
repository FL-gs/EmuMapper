package com.example.pairingapp.core.settings

import androidx.annotation.StringRes
import com.example.pairingapp.R

enum class AppLanguage(@get:StringRes val labelRes: Int) {
    SYSTEM(R.string.language_system),
    FR(R.string.language_french),
    EN(R.string.language_english)
}

