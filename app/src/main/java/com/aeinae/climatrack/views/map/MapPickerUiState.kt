package com.aeinae.climatrack.views.map

import com.aeinae.climatrack.utils.Constants

data class MapPickerUiState(
    val initialLat: Double = Constants.DefaultSettings.LATITUDE,
    val initialLon: Double = Constants.DefaultSettings.LONGITUDE,
    val isInitialPositionLoaded: Boolean = false,
    val selectedLat: Double? = null,
    val selectedLon: Double? = null,
    val selectedCityName: String? = null,
    val isReverseGeocoding: Boolean = false,
    val saved: Boolean = false
)