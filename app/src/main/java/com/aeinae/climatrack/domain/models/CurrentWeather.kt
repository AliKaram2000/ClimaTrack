package com.aeinae.climatrack.domain.models

data class CurrentWeather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val cloudiness: Int,
    val dateTime: Long
)