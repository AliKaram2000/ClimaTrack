package com.aeinae.climatrack.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val locationKey: String,

    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,

    val currentWeather: String,
    val hourlyForecast: String,
    val dailyForecast: String,

    val lastUpdated: Long,
)