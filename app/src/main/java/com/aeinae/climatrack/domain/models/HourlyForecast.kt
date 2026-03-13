package com.aeinae.climatrack.domain.models

data class HourlyForecast(
    val dateTime: Long,
    val temperature: Double,
    val iconCode: String,
    val description: String
)