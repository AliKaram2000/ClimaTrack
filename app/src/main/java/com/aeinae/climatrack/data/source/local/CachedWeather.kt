package com.aeinae.climatrack.data.source.local

import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse

data class CachedWeather (
    val current: CurrentWeatherResponse,
    val forecast: ForecastResponse,
    val lastUpdated: Long
)