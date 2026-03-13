package com.aeinae.climatrack.data.repository

import com.aeinae.climatrack.data.local.preferences.SettingsPreferences
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsPreferences: SettingsPreferences) {

    val locationModeFlow: Flow<String> = settingsPreferences.locationMode

    val mapLatitudeFlow: Flow<Double> = settingsPreferences.mapLatitude

    val mapLongitudeFlow: Flow<Double> = settingsPreferences.mapLongitude

    val tempUnitFlow: Flow<String> = settingsPreferences.tempUnit

    val windUnitFlow: Flow<String> = settingsPreferences.windUnit

    val languageFlow: Flow<String> = settingsPreferences.language

    suspend fun setLocationMode(mode: String) {
        settingsPreferences.setLocationMode(mode)
    }

    suspend fun setMapLocation(lat: Double, lon: Double) {
        settingsPreferences.setMapLocation(lat, lon)
    }

    suspend fun setTempUnit(unit: String) {
        settingsPreferences.setTempUnit(unit)
    }

    suspend fun setWindUnit(unit: String) {
        settingsPreferences.setWindUnit(unit)
    }

    suspend fun setLanguage(language: String) {
        settingsPreferences.setLanguage(language)
    }
}