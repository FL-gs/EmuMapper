package com.example.pairingapp.features.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.pairingapp.R
import com.example.pairingapp.core.input.PadKey
import com.example.pairingapp.core.input.mapKeyEvent
import com.example.pairingapp.core.settings.AppLanguage
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.core.ui.components.rememberHintsForState
import com.example.pairingapp.features.settings.app.AppSettingsScreen
import com.example.pairingapp.features.settings.debug.DebugScreen
import com.example.pairingapp.features.settings.emulators.EmulatorsScreen
import com.example.pairingapp.features.settings.layout.SettingsLayout
import com.example.pairingapp.features.settings.layout.SettingsSidebar

private enum class SettingsCategory(@get:StringRes val labelRes: Int) {
    APPEARANCE(R.string.settings_category_App),
    EMULATORS(R.string.settings_category_emulators),
    DEBUG(R.string.settings_category_debug)
}

private enum class SettingsMode {
    HUB,
    CATEGORY
}

@Composable
fun SettingsHomeScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    darkTheme: Boolean,
    onSetDarkTheme: (Boolean) -> Unit,
    language: AppLanguage,
    onSetLanguage: (AppLanguage) -> Unit,
    enabledEmulators: Set<String>,
    onSetEnabledEmulators: (Set<String>) -> Unit,
    writeMode: WriteMode,
    onSetWriteMode: (WriteMode) -> Unit,
    internalController: String?,
    onSetInternalController: (String?) -> Unit,
    debugLogs: Boolean,
    onSetDebugLogs: (Boolean) -> Unit,
    onClearLogs: () -> Unit,
) {
    val settingsTitle = stringResource(R.string.settings_sidebar_title)

    var selectedName by rememberSaveable { mutableStateOf(SettingsCategory.APPEARANCE.name) }
    var modeName by rememberSaveable { mutableStateOf(SettingsMode.HUB.name) }

    val selected = SettingsCategory.valueOf(selectedName)
    val mode = SettingsMode.valueOf(modeName)

    val focusRequester = remember { FocusRequester() }

    var contentHintState by remember { mutableStateOf<HintBarState>(HintBarState.SettingsHome) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(mode) {
        if (mode == SettingsMode.HUB) {
            contentHintState = HintBarState.SettingsHome
        }
    }

    val categories = SettingsCategory.entries
    val selectedIndex = categories.indexOf(selected)

    fun moveSelection(delta: Int) {
        val next = (selectedIndex + delta).coerceIn(0, categories.lastIndex)
        selectedName = categories[next].name
    }

    val footerHints = rememberHintsForState(
        if (mode == SettingsMode.HUB) HintBarState.SettingsHome else contentHintState
    )

    SettingsLayout(
        modifier = modifier
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                val key = mapKeyEvent(event.nativeKeyEvent)

                return@onPreviewKeyEvent when (mode) {
                    SettingsMode.HUB -> {
                        when (key) {
                            PadKey.UP -> {
                                moveSelection(-1)
                                true
                            }

                            PadKey.DOWN -> {
                                moveSelection(+1)
                                true
                            }

                            PadKey.A -> {
                                modeName = SettingsMode.CATEGORY.name
                                true
                            }

                            PadKey.B -> {
                                onBack()
                                true
                            }

                            else -> false
                        }
                    }

                    SettingsMode.CATEGORY -> {
                        when (key) {
                            PadKey.B -> {
                                modeName = SettingsMode.HUB.name
                                focusRequester.requestFocus()
                                true
                            }

                            else -> false
                        }
                    }
                }
            }
            .focusable(),
        footerHints = footerHints,
        sidebar = {
            SettingsSidebar(
                title = settingsTitle,
                items = categories.map { stringResource(it.labelRes) },
                selectedIndex = selectedIndex,
                isHub = (mode == SettingsMode.HUB)
            )
        },
        content = {
            when (selected) {
                SettingsCategory.APPEARANCE -> {
                    AppSettingsScreen(
                        active = (mode == SettingsMode.CATEGORY),
                        darkTheme = darkTheme,
                        onSetDarkTheme = onSetDarkTheme,
                        language = language,
                        onSetLanguage = onSetLanguage,
                        writeMode = writeMode,
                        onSetWriteMode = onSetWriteMode,
                        internalController = internalController,
                        onSetInternalController = onSetInternalController,
                        onHintStateChanged = { state ->
                            if (mode == SettingsMode.CATEGORY) {
                                contentHintState = state
                            }
                        }
                    )
                }

                SettingsCategory.EMULATORS -> {
                    EmulatorsScreen(
                        active = (mode == SettingsMode.CATEGORY),
                        enabledPackages = enabledEmulators,
                        onSetEnabledPackages = onSetEnabledEmulators,
                        onHintStateChanged = { state ->
                            if (mode == SettingsMode.CATEGORY) {
                                contentHintState = state
                            }
                        }
                    )
                }

                SettingsCategory.DEBUG -> {
                    DebugScreen(
                        active = (mode == SettingsMode.CATEGORY),
                        debugLogs = debugLogs,
                        onSetDebugLogs = onSetDebugLogs,
                        onClearLogs = onClearLogs,
                        onHintStateChanged = { state ->
                            if (mode == SettingsMode.CATEGORY) {
                                contentHintState = state
                            }
                        }
                    )
                }
            }
        }
    )
}
