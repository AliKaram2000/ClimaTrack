package com.aeinae.climatrack.views.favourites.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aeinae.climatrack.app.ClimaTrackApplication
import com.aeinae.climatrack.data.remote.dto.geocoding.GeocodingResponseDto
import com.aeinae.climatrack.data.repository.FavoriteRepository
import com.aeinae.climatrack.data.repository.GeocodingRepository
import com.aeinae.climatrack.data.local.database.entity.FavoriteEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddFavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFavoriteUiState())
    val uiState: StateFlow<AddFavoriteUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var reverseGeocodeJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        reverseGeocodeJob?.cancel()
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isSearching = true) }
            try {
                Log.d("AddFavorite", "Searching for: $query")
                val result = geocodingRepository.searchCities(query)
                Log.d("AddFavorite", "Search result: isSuccess=${result.isSuccess}, size=${result.getOrNull()?.size}")
                if (result.isFailure) {
                    Log.e("AddFavorite", "Search error: ${result.exceptionOrNull()?.message}")
                }
                _uiState.update {
                    it.copy(
                        searchResults = result.getOrDefault(emptyList()),
                        isSearching = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AddFavorite", "Search exception: ${e.message}", e)
                _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            }
        }
    }

    fun onSearchResultSelected(dto: GeocodingResponseDto) {
        searchJob?.cancel()
        reverseGeocodeJob?.cancel()

        _uiState.update {
            it.copy(
                selectedLat = dto.lat,
                selectedLon = dto.lon,
                selectedCityName = dto.name,
                selectedCountry = dto.country,
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false,
                isReverseGeocoding = false
            )
        }
    }

    fun onMapTapped(lat: Double, lon: Double) {
        searchJob?.cancel()
        reverseGeocodeJob?.cancel()

        _uiState.update {
            it.copy(
                selectedLat = lat,
                selectedLon = lon,
                selectedCityName = null,
                selectedCountry = null,
                searchQuery = "",
                searchResults = emptyList(),
                isSearching = false
            )
        }

        reverseGeocodeJob = viewModelScope.launch {
            _uiState.update { it.copy(isReverseGeocoding = true) }
            try {
                Log.d("AddFavorite", "Reverse geocoding: $lat, $lon")
                val result = geocodingRepository.reverseGeocode(lat, lon)
                Log.d("AddFavorite", "Reverse result: isSuccess=${result.isSuccess}, dto=${result.getOrNull()}")
                if (result.isFailure) {
                    Log.e("AddFavorite", "Reverse error: ${result.exceptionOrNull()?.message}")
                }
                val dto = result.getOrNull()
                _uiState.update {
                    it.copy(
                        selectedCityName = dto?.name,
                        selectedCountry = dto?.country,
                        isReverseGeocoding = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AddFavorite", "Reverse exception: ${e.message}", e)
                _uiState.update { it.copy(isReverseGeocoding = false) }
            }
        }
    }

    fun onConfirm() {
        val state = _uiState.value
        val lat = state.selectedLat ?: return
        val lon = state.selectedLon ?: return
        val cityName = state.selectedCityName ?: return
        val country = state.selectedCountry ?: "—"

        viewModelScope.launch {
            favoriteRepository.addFavorite(
                FavoriteEntity(
                    cityName = cityName,
                    country = country,
                    latitude = lat,
                    longitude = lon
                )
            )
            _uiState.update { it.copy(saved = true) }
        }
    }

    companion object {
        fun factory(application: ClimaTrackApplication): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AddFavoriteViewModel::class.java)) {
                        return AddFavoriteViewModel(
                            favoriteRepository = application.favoriteRepository,
                            geocodingRepository = application.geocodingRepository
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}