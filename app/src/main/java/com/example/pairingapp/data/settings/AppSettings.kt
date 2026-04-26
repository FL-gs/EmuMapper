package com.example.pairingapp.data.settings

import com.example.pairingapp.core.settings.AppLanguage
import com.example.pairingapp.core.settings.WriteMode

data class AppSettings(
    val darkTheme: Boolean = false,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val enabledEmulators: Set<String> = emptySet(),

    // Onboarding + manettes internes
    val onboardingDone: Boolean = false,
    val internalController1: String? = null,
    val internalController2: String? = null,

    val writeMode: WriteMode = WriteMode.MANUAL,

    val debugLogs: Boolean = false
)
