package dev.emumapper.app.core.settings

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

fun Context.localized(language: AppLanguage): Context {
    if (language == AppLanguage.SYSTEM) return this

    val locale = when (language) {
        AppLanguage.FR -> Locale.FRENCH
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.SYSTEM -> Locale.getDefault()
    }

    Locale.setDefault(locale)

    val config = Configuration(resources.configuration)
    config.setLocales(LocaleList(locale))

    return createConfigurationContext(config)
}