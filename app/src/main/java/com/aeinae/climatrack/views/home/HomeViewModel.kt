package com.aeinae.climatrack.views.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.data.location.LocationService
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

class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentLocationMode: String = Constants.DefaultSettings.LOCATION_MODE
    private var currentMapLat: Double = Constants.DefaultSettings.LATITUDE
    private var currentMapLon: Double = Constants.DefaultSettings.LONGITUDE
    private var currentTempUnit: String = Constants.DefaultSettings.TEMP_UNIT
    private var currentWindUnit: String = Constants.DefaultSettings.WIND_UNIT
    private var currentLanguage: String = Constants.DefaultSettings.LANGUAGE

    init {
        observeSettingsAndFetch()
    }

    private fun observeSettingsAndFetch() {
        viewModelScope.launch {
            combine(
                settingsRepository.locationModeFlow,
                settingsRepository.mapLatitudeFlow,
                settingsRepository.mapLongitudeFlow,
                settingsRepository.tempUnitFlow,
                settingsRepository.windUnitFlow,
                settingsRepository.languageFlow
            ) { values ->
                SettingsSnapshot(
                    locationMode = values[0] as String,
                    mapLat = values[1] as Double,
                    mapLon = values[2] as Double,
                    tempUnit = values[3] as String,
                    windUnit = values[4] as String,
                    language = values[5] as String
                )
            }.collectLatest { snapshot ->
                currentLocationMode = snapshot.locationMode
                currentMapLat = snapshot.mapLat
                currentMapLon = snapshot.mapLon
                currentTempUnit = snapshot.tempUnit
                currentWindUnit = snapshot.windUnit
                currentLanguage = snapshot.language
                fetchWeather(snapshot, showLoading = true)
            }
        }
    }

    private suspend fun fetchWeather(snapshot: SettingsSnapshot, showLoading: Boolean) {
        if (showLoading) {
            _uiState.value = HomeUiState.Loading
        }

        val locationResult = resolveLocation(snapshot)
        if (locationResult.isFailure) {
            _uiState.value = HomeUiState.Error(
                locationResult.exceptionOrNull()?.message ?: "Unable to determine location"
            )
            return
        }

        val (lat, lon) = locationResult.getOrThrow()
        val result = weatherRepository.getWeather(
            lat = lat,
            lon = lon,
            units = snapshot.tempUnit,
            lang = snapshot.language,
            key = Constants.HOME_CACHE_KEY
        )

        when (result) {
            is WeatherResult.Success -> {
                _uiState.value = HomeUiState.Success(
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
                _uiState.value = HomeUiState.Error(result.message)
            }
        }
    }

    private suspend fun resolveLocation(snapshot: SettingsSnapshot): Result<Pair<Double, Double>> {
        return if (snapshot.locationMode == Constants.LocationMode.GPS) {
            locationService.getCurrentLocation()
        } else {
            Result.success(Pair(snapshot.mapLat, snapshot.mapLon))
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchWeather(
                    SettingsSnapshot(
                        locationMode = currentLocationMode,
                        mapLat = currentMapLat,
                        mapLon = currentMapLon,
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

    fun requiresGpsPermission(): Boolean {
        return currentLocationMode == Constants.LocationMode.GPS
    }

    private data class SettingsSnapshot(
        val locationMode: String,
        val mapLat: Double,
        val mapLon: Double,
        val tempUnit: String,
        val windUnit: String,
        val language: String
    )

    companion object {
        fun factory(application: ClimaTrackApplication): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                        return HomeViewModel(
                            weatherRepository = application.weatherRepository,
                            settingsRepository = application.settingsRepository,
                            locationService = application.locationService
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}