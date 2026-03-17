package com.aeinae.climatrack.app

import android.app.Application
import com.aeinae.climatrack.data.local.database.AppDataBase
import com.aeinae.climatrack.data.local.preferences.SettingsPreferences
import com.aeinae.climatrack.data.location.LocationService
import com.aeinae.climatrack.data.remote.RetrofitClient
import com.aeinae.climatrack.data.repository.FavoriteRepository
import com.aeinae.climatrack.data.repository.GeocodingRepository
import com.aeinae.climatrack.data.repository.SettingsRepository
import com.aeinae.climatrack.data.repository.WeatherRepository
import com.aeinae.climatrack.data.source.local.WeatherLocalDataSource
import com.aeinae.climatrack.data.source.remote.GeocodingRemoteDataSource
import com.aeinae.climatrack.data.source.remote.WeatherRemoteDataSource
import com.google.gson.Gson

class ClimaTrackApplication: Application() {

    val gson by lazy { Gson() }

    private val database by lazy { AppDataBase.getInstance(this) }

    private val service by lazy { RetrofitClient.service }

    private val weatherCacheDao by lazy { database.weatherCacheDao() }

    private val favoriteDao by lazy { database.favoriteDao() }

    val settingsPreferences by lazy { SettingsPreferences(this) }

    private val weatherRemoteDataSource by lazy { WeatherRemoteDataSource(service) }

    private val geocodingRemoteDataSource by lazy { GeocodingRemoteDataSource(service) }

    private val weatherLocalDataSource by lazy { WeatherLocalDataSource(weatherCacheDao, gson) }

    val weatherRepository by lazy {
        WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource)
    }

    val favoriteRepository by lazy {
        FavoriteRepository(favoriteDao, weatherLocalDataSource)
    }

    val geocodingRepository by lazy {
        GeocodingRepository(geocodingRemoteDataSource)
    }

    val settingsRepository by lazy {
        SettingsRepository(settingsPreferences)
    }

    val locationService by lazy { LocationService(this) }
}