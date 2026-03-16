package com.aeinae.climatrack.views.favourites.details

import com.aeinae.climatrack.domain.models.CurrentWeather
import com.aeinae.climatrack.domain.models.DailyForecast
import com.aeinae.climatrack.domain.models.HourlyForecast

sealed interface FavoriteDetailUiState {
    data object Loading : FavoriteDetailUiState

    data class Success(
        val current: CurrentWeather,
        val hourly: List<HourlyForecast>,
        val daily: List<DailyForecast>,
        val isFromCache: Boolean,
        val lastUpdated: Long,
        val windUnit: String,
        val tempUnit: String
    ) : FavoriteDetailUiState

    data class Error(val message: String) : FavoriteDetailUiState
}