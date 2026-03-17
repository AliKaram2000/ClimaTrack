package com.aeinae.climatrack.views.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.ui.theme.ClimaTrackTheme
import com.aeinae.climatrack.utils.Constants



private data class SettingOption(val value: String, val label: String)

private val locationOptions = listOf(
    SettingOption(Constants.LocationMode.GPS, "Use GPS Location"),
    SettingOption(Constants.LocationMode.MAP, "Choose from Map")
)

private val tempOptions = listOf(
    SettingOption(Constants.Units.STANDARD, "Kelvin"),
    SettingOption(Constants.Units.METRIC, "Celsius"),
    SettingOption(Constants.Units.IMPERIAL, "Fahrenheit")
)

private val windOptions = listOf(
    SettingOption(Constants.WindUnits.METER_SEC, "meter/sec"),
    SettingOption(Constants.WindUnits.MILES_HOUR, "miles/hour")
)

private val languageOptions = listOf(
    SettingOption(Constants.Languages.ENGLISH, "English"),
    SettingOption(Constants.Languages.ARABIC, "Arabic (العربية)")
)



@Composable
fun SettingsScreen(onNavigateToMapPicker: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as ClimaTrackApplication
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(application))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SettingsUiState.Loading -> LoadingContent()
        is SettingsUiState.Loaded -> LoadedContent(
            state = state,
            onLocationModeChange = { mode ->
                viewModel.setLocationMode(mode)
                if (mode == Constants.LocationMode.MAP) {
                    onNavigateToMapPicker()
                }
            },
            onNavigateToMapPicker = onNavigateToMapPicker,
            onTempUnitChange = viewModel::setTempUnit,
            onWindUnitChange = viewModel::setWindUnit,
            onLanguageChange = viewModel::setLanguage
        )
    }
}




@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = ClimaTrackTheme.extendedColors.accent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(40.dp)
        )
    }
}



@Composable
private fun LoadedContent(
    state: SettingsUiState.Loaded,
    onLocationModeChange: (String) -> Unit,
    onNavigateToMapPicker: () -> Unit,
    onTempUnitChange: (String) -> Unit,
    onWindUnitChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val dimens = ClimaTrackTheme.dimens
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = dimens.spacing16)
            .padding(top = dimens.spacing24, bottom = dimens.spacing32)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(dimens.spacing24))

        SettingSectionLabel("Location")
        Spacer(modifier = Modifier.height(dimens.spacing8))
        SettingsCard {
            LocationSelector(
                selectedMode = state.locationMode,
                onModeChange = onLocationModeChange,
                onMapPickerClick = onNavigateToMapPicker
            )
        }

        Spacer(modifier = Modifier.height(dimens.spacing24))

        SettingSectionLabel("Units")
        Spacer(modifier = Modifier.height(dimens.spacing8))
        SettingsCard {
            Text(
                text = "Temperature",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(dimens.spacing12))
            SegmentedSelector(
                options = tempOptions,
                selectedValue = state.tempUnit,
                onSelect = onTempUnitChange
            )

            Spacer(modifier = Modifier.height(dimens.spacing16))
            HorizontalDivider(
                color = ClimaTrackTheme.extendedColors.divider,
                thickness = dimens.hairlineHeight
            )
            Spacer(modifier = Modifier.height(dimens.spacing16))

            Text(
                text = "Wind Speed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(dimens.spacing12))
            SegmentedSelector(
                options = windOptions,
                selectedValue = state.windUnit,
                onSelect = onWindUnitChange
            )
        }

        Spacer(modifier = Modifier.height(dimens.spacing24))

        SettingSectionLabel("Language")
        Spacer(modifier = Modifier.height(dimens.spacing8))
        SettingsCard {
            RadioGroup(
                options = languageOptions,
                selectedValue = state.language,
                onSelect = onLanguageChange
            )
        }
    }
}



@Composable
private fun LocationSelector(
    selectedMode: String,
    onModeChange: (String) -> Unit,
    onMapPickerClick: () -> Unit
) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors
    val isMapMode = selectedMode == Constants.LocationMode.MAP

    Column {
        locationOptions.forEach { option ->
            val isSelected = option.value == selectedMode

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onModeChange(option.value) }
                    .padding(vertical = dimens.spacing8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (option.value == Constants.LocationMode.MAP && isMapMode) {
                Text(
                    text = "Select on Map",
                    style = MaterialTheme.typography.bodySmall,
                    color = extendedColors.accent,
                    modifier = Modifier
                        .padding(start = 48.dp, bottom = dimens.spacing4)
                        .clickable { onMapPickerClick() }
                )
            }
        }
    }
}



@Composable
private fun SettingSectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = ClimaTrackTheme.extendedColors.accent,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val dimens = ClimaTrackTheme.dimens
    val extendedColors = ClimaTrackTheme.extendedColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(extendedColors.cardBackground)
            .border(
                width = dimens.hairlineHeight,
                color = extendedColors.cardStroke,
                shape = MaterialTheme.shapes.medium
            )
            .padding(dimens.spacing16),
        content = content
    )
}

@Composable
private fun RadioGroup(
    options: List<SettingOption>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    val dimens = ClimaTrackTheme.dimens

    Column {
        options.forEach { option ->
            val isSelected = option.value == selectedValue

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!isSelected) onSelect(option.value)
                    }
                    .padding(vertical = dimens.spacing8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SegmentedSelector(
    options: List<SettingOption>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    val dimens = ClimaTrackTheme.dimens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = dimens.cardStrokeWidth,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = option.value == selectedValue

            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(dimens.cardStrokeWidth)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable {
                        if (!isSelected) onSelect(option.value)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}