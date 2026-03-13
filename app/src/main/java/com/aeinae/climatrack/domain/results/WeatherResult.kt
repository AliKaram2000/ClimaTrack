package com.aeinae.climatrack.domain.results

import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse

sealed class WeatherResult {
    data class Success(
        val current: CurrentWeatherResponse,
        val forecast: ForecastResponse,
        val isFromCache: Boolean,
        val lastUpdated: Long,
    ) : WeatherResult()

    data class Error(
        val message: String,
    ) : WeatherResult()
}