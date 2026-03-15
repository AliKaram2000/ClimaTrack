package com.aeinae.climatrack.views.home

import com.aeinae.climatrack.domain.models.CurrentWeather
import com.aeinae.climatrack.domain.models.DailyForecast
import com.aeinae.climatrack.domain.models.HourlyForecast

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val current: CurrentWeather,
        val hourly: List<HourlyForecast>,
        val daily: List<DailyForecast>,
        val isFromCache: Boolean,
        val lastUpdated: Long,
        val windUnit: String,
        val tempUnit: String
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}