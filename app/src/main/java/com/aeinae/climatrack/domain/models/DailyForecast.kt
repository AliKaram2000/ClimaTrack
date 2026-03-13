package com.aeinae.climatrack.domain.models

data class DailyForecast(
    val dateTime: Long,
    val tempMin: Double,
    val tempMax: Double,
    val iconCode: String,
    val description: String
)