package com.aeinae.climatrack.views.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.data.repository.GeocodingRepository
import com.aeinae.climatrack.data.repository.SettingsRepository
import com.aeinae.climatrack.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapPickerViewModel(
    private val settingsRepository: SettingsRepository,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapPickerUiState())
    val uiState: StateFlow<MapPickerUiState> = _uiState.asStateFlow()

    private var reverseGeocodeJob: Job? = null

    init {
        loadInitialPosition()
    }

    private fun loadInitialPosition() {
        viewModelScope.launch {
            val lat = settingsRepository.mapLatitudeFlow.first()
            val lon = settingsRepository.mapLongitudeFlow.first()
            _uiState.update {
                it.copy(initialLat = lat, initialLon = lon, isInitialPositionLoaded = true)
            }
        }
    }

    fun onMapTapped(lat: Double, lon: Double) {
        _uiState.update {
            it.copy(
                selectedLat = lat,
                selectedLon = lon,
                selectedCityName = null
            )
        }

        reverseGeocodeJob?.cancel()
        reverseGeocodeJob = viewModelScope.launch {
            _uiState.update { it.copy(isReverseGeocoding = true) }
            try {
                val result = geocodingRepository.reverseGeocode(lat, lon)
                val dto = result.getOrNull()
                val cityName = if (dto != null) {
                    buildString {
                        append(dto.name ?: "Unknown")
                        dto.country?.let { append(", $it") }
                    }
                } else {
                    null
                }
                _uiState.update { it.copy(selectedCityName = cityName, isReverseGeocoding = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isReverseGeocoding = false) }
            }
        }
    }

    fun onConfirm() {
        val lat = _uiState.value.selectedLat ?: return
        val lon = _uiState.value.selectedLon ?: return

        viewModelScope.launch {
            settingsRepository.setMapLocation(lat, lon)
            settingsRepository.setLocationMode(Constants.LocationMode.MAP)
            _uiState.update { it.copy(saved = true) }
        }
    }

    companion object {
        fun factory(application: ClimaTrackApplication): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MapPickerViewModel::class.java)) {
                        return MapPickerViewModel(
                            settingsRepository = application.settingsRepository,
                            geocodingRepository = application.geocodingRepository
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}