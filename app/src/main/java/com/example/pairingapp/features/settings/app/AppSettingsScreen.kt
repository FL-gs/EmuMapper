package com.example.pairingapp.features.settings.app

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pairingapp.R
import com.example.pairingapp.core.input.internalControllerLabel
import com.example.pairingapp.core.settings.AppLanguage
import com.example.pairingapp.core.settings.WriteMode
import com.example.pairingapp.core.ui.components.ActionButton
import com.example.pairingapp.core.ui.components.AppSettingsFocusZone
import com.example.pairingapp.core.ui.components.HintBarState
import com.example.pairingapp.core.ui.components.SectionDivider
import com.example.pairingapp.features.components.DevicePickerViewModel
import com.example.pairingapp.features.components.SelectionDialog
import com.example.pairingapp.features.settings.components.SettingRow

private enum class ThemeOption(@get:StringRes val labelRes: Int) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark)
}

@Composable
fun AppSettingsScreen(
    modifier: Modifier = Modifier,
    active: Boolean,
    darkTheme: Boolean,
    onSetDarkTheme: (Boolean) -> Unit,
    language: AppLanguage,
    onSetLanguage: (AppLanguage) -> Unit,
    writeMode: WriteMode,
    onSetWriteMode: (WriteMode) -> Unit,
    internalController: String?,
    onSetInternalController: (String?) -> Unit,
    onHintStateChanged: (HintBarState) -> Unit
) {
    val noneLabel = stringResource(R.string.none)
    val internalControllerDialogTitle = stringResource(R.string.internal_controllers_title)
    val previewAlpha = if (active) 1f else 0.35f

    val focusRequesters = remember { List(4) { FocusRequester() } }

    val pickerViewModel: DevicePickerViewModel = viewModel()
    val showDialog by pickerViewModel.showDialog.collectAsState()
    val choices by pickerViewModel.choices.collectAsState()

    var focusedIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(active, showDialog) {
        if (active && !showDialog) {
            focusRequesters[focusedIndex].requestFocus()

            onHintStateChanged(
                if (focusedIndex >= 3) {
                    HintBarState.AppSettings(AppSettingsFocusZone.CONTROLLER_BUTTON)
                } else {
                    HintBarState.AppSettings(AppSettingsFocusZone.SETTINGS_ROW)
                }
            )
        }
    }

    val themes = remember {
        listOf(
            ThemeOption.LIGHT,
            ThemeOption.DARK
        )
    }

    val languages = remember {
        listOf(
            AppLanguage.SYSTEM,
            AppLanguage.EN,
            AppLanguage.FR
        )
    }

    val writeModes = remember {
        WriteMode.entries
    }

    val currentTheme = if (darkTheme) ThemeOption.DARK else ThemeOption.LIGHT
    val currentThemeIndex = themes.indexOf(currentTheme).coerceAtLeast(0)
    val currentLanguageIndex = languages.indexOf(language).coerceAtLeast(0)
    val currentWriteModeIndex = writeModes.indexOf(writeMode).coerceAtLeast(0)
    val currentWriteMode = writeModes[currentWriteModeIndex]

    fun selectTheme(index: Int): Boolean {
        if (index !in themes.indices) return false
        onSetDarkTheme(themes[index] == ThemeOption.DARK)
        return true
    }

    fun selectLanguage(index: Int): Boolean {
        if (index !in languages.indices) return false
        onSetLanguage(languages[index])
        return true
    }

    fun selectWriteMode(index: Int): Boolean {
        if (index !in writeModes.indices) return false
        onSetWriteMode(writeModes[index])
        return true
    }

    fun openControllerPicker() {
        if (!active) return
        pickerViewModel.open(noneLabel)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 36.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.graphicsLayer { alpha = previewAlpha }
            ) {
                SettingRow(
                    title = stringResource(R.string.theme),
                    subtitle = stringResource(R.string.theme_subtitle),
                    value = stringResource(themes[currentThemeIndex].labelRes),
                    active = active,
                    onFocused = {
                        focusedIndex = 0
                        onHintStateChanged(HintBarState.AppSettings(AppSettingsFocusZone.SETTINGS_ROW))
                    },
                    focusRequester = focusRequesters[0],
                    hasPreviousValue = currentThemeIndex > 0,
                    hasNextValue = currentThemeIndex < themes.lastIndex,
                    onPreviousValue = {
                        selectTheme(currentThemeIndex - 1)
                    },
                    onNextValue = {
                        selectTheme(currentThemeIndex + 1)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingRow(
                    title = stringResource(R.string.settings_category_language),
                    subtitle = stringResource(R.string.language_subtitle),
                    value = stringResource(languages[currentLanguageIndex].labelRes),
                    active = active,
                    onFocused = {
                        focusedIndex = 1
                        onHintStateChanged(HintBarState.AppSettings(AppSettingsFocusZone.SETTINGS_ROW))
                    },
                    focusRequester = focusRequesters[1],
                    hasPreviousValue = currentLanguageIndex > 0,
                    hasNextValue = currentLanguageIndex < languages.lastIndex,
                    onPreviousValue = {
                        selectLanguage(currentLanguageIndex - 1)
                    },
                    onNextValue = {
                        selectLanguage(currentLanguageIndex + 1)
                    }
                )
            }

            SectionDivider()

            Column(
                modifier = Modifier.graphicsLayer { alpha = previewAlpha }
            ) {
                SettingRow(
                    title = stringResource(R.string.settings_category_write_mode),
                    subtitle = buildString {
                        append(stringResource(R.string.write_mode_subtitle))
                        append("\n")
                        append(stringResource(currentWriteMode.descriptionRes))
                    },
                    value = stringResource(writeModes[currentWriteModeIndex].labelRes),
                    active = active,
                    onFocused = {
                        focusedIndex = 2
                        onHintStateChanged(HintBarState.AppSettings(AppSettingsFocusZone.SETTINGS_ROW))
                    },
                    focusRequester = focusRequesters[2],
                    hasPreviousValue = currentWriteModeIndex > 0,
                    hasNextValue = currentWriteModeIndex < writeModes.lastIndex,
                    onPreviousValue = {
                        selectWriteMode(currentWriteModeIndex - 1)
                    },
                    onNextValue = {
                        selectWriteMode(currentWriteModeIndex + 1)
                    }
                )
            }

            SectionDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .graphicsLayer { alpha = previewAlpha }
            ) {
                Text(
                    text = stringResource(R.string.controllers_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.controllers_subtitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionButton(
                    text = internalControllerLabel(
                        internalController,
                        choices,
                        noneLabel
                    ),
                    selected = false,
                    active = active,
                    onFocused = {
                        focusedIndex = 3
                        onHintStateChanged(HintBarState.AppSettings(AppSettingsFocusZone.CONTROLLER_BUTTON))
                    },
                    focusRequester = focusRequesters[3],
                    previousFocusRequester = focusRequesters[2],
                    onClick = {
                        openControllerPicker()
                    }
                )
            }
        }

        if (showDialog) {
            SelectionDialog(
                title = internalControllerDialogTitle,
                choices = choices,
                current = internalController,
                onPick = { picked ->
                    onSetInternalController(picked)
                    pickerViewModel.close()
                },
                onDismiss = {
                    pickerViewModel.close()
                }
            )
        }
    }
}
