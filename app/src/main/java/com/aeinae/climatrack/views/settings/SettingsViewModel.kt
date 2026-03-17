package com.aeinae.climatrack.views.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.data.repository.SettingsRepository
import com.aeinae.climatrack.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.locationModeFlow,
                settingsRepository.tempUnitFlow,
                settingsRepository.windUnitFlow,
                settingsRepository.languageFlow
            ) { locationMode, tempUnit, windUnit, language ->
                SettingsUiState.Loaded(
                    locationMode = locationMode,
                    tempUnit = tempUnit,
                    windUnit = windUnit,
                    language = language
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setLocationMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setLocationMode(mode)
        }
    }

    fun setTempUnit(unit: String) {
        viewModelScope.launch {
            settingsRepository.setTempUnit(unit)
            weatherRepository.clearAllCache()
        }
    }

    fun setWindUnit(unit: String) {
        viewModelScope.launch {
            settingsRepository.setWindUnit(unit)
            weatherRepository.clearAllCache()
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            weatherRepository.clearAllCache()
        }
    }

    companion object {
        fun factory(application: ClimaTrackApplication): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                        return SettingsViewModel(
                            settingsRepository = application.settingsRepository,
                            weatherRepository = application.weatherRepository
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}