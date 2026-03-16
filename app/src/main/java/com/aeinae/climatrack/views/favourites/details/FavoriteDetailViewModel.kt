package com.aeinae.climatrack.views.favourites.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.data.repository.FavoriteRepository
import com.aeinae.climatrack.data.repository.SettingsRepository
import com.aeinae.climatrack.data.repository.WeatherRepository
import com.aeinae.climatrack.domain.mappers.WeatherMapper
import com.aeinae.climatrack.domain.results.WeatherResult
import com.aeinae.climatrack.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FavoriteDetailViewModel(
    private val favoriteId: Int,
    private val favoriteRepository: FavoriteRepository,
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoriteDetailUiState>(FavoriteDetailUiState.Loading)
    val uiState: StateFlow<FavoriteDetailUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var currentTempUnit: String = Constants.DefaultSettings.TEMP_UNIT
    private var currentWindUnit: String = Constants.DefaultSettings.WIND_UNIT
    private var currentLanguage: String = Constants.DefaultSettings.LANGUAGE

    init {
        loadFavoriteAndObserveSettings()
    }

    private fun loadFavoriteAndObserveSettings() {
        viewModelScope.launch {
            val favorite = favoriteRepository.getFavorite(favoriteId)
            if (favorite == null) {
                _uiState.value = FavoriteDetailUiState.Error("Favourite not found")
                return@launch
            }

            lat = favorite.latitude
            lon = favorite.longitude

            combine(
                settingsRepository.tempUnitFlow,
                settingsRepository.windUnitFlow,
                settingsRepository.languageFlow
            ) { tempUnit, windUnit, language ->
                SettingsSnapshot(
                    tempUnit = tempUnit,
                    windUnit = windUnit,
                    language = language
                )
            }.collectLatest { snapshot ->
                currentTempUnit = snapshot.tempUnit
                currentWindUnit = snapshot.windUnit
                currentLanguage = snapshot.language
                fetchWeather(snapshot, showLoading = true)
            }
        }
    }

    private suspend fun fetchWeather(snapshot: SettingsSnapshot, showLoading: Boolean) {
        if (showLoading) {
            _uiState.value = FavoriteDetailUiState.Loading
        }

        val result = weatherRepository.getWeather(
            lat = lat,
            lon = lon,
            units = snapshot.tempUnit,
            lang = snapshot.language,
            key = Constants.favoriteCacheKey(favoriteId)
        )

        when (result) {
            is WeatherResult.Success -> {
                _uiState.value = FavoriteDetailUiState.Success(
                    current = WeatherMapper.mapCurrentWeather(result.current),
                    hourly = WeatherMapper.mapHourlyForecasts(result.forecast),
                    daily = WeatherMapper.mapDailyForecasts(result.forecast),
                    isFromCache = result.isFromCache,
                    lastUpdated = result.lastUpdated,
                    windUnit = snapshot.windUnit,
                    tempUnit = snapshot.tempUnit
                )
            }
            is WeatherResult.Error -> {
                _uiState.value = FavoriteDetailUiState.Error(result.message)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchWeather(
                    SettingsSnapshot(
                        tempUnit = currentTempUnit,
                        windUnit = currentWindUnit,
                        language = currentLanguage
                    ),
                    showLoading = false
                )
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private data class SettingsSnapshot(
        val tempUnit: String,
        val windUnit: String,
        val language: String
    )

    companion object {
        fun factory(favoriteId: Int, application: ClimaTrackApplication): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(FavoriteDetailViewModel::class.java)) {
                        return FavoriteDetailViewModel(
                            favoriteId = favoriteId,
                            favoriteRepository = application.favoriteRepository,
                            weatherRepository = application.weatherRepository,
                            settingsRepository = application.settingsRepository
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}