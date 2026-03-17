package com.aeinae.climatrack.views.favourites.add

import com.aeinae.climatrack.data.remote.dto.geocoding.GeocodingResponseDto

data class AddFavoriteUiState(

    val searchQuery: String = "",
    val searchResults: List<GeocodingResponseDto> = emptyList(),
    val isSearching: Boolean = false,

    val selectedLat: Double? = null,
    val selectedLon: Double? = null,

    val selectedCityName: String? = null,
    val selectedCountry: String? = null,

    val isReverseGeocoding: Boolean = false,

    val saved: Boolean = false
)