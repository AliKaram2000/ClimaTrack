package com.aeinae.climatrack.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aeinae.climatrack.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingsPreferences(val context: Context) {

    private object Keys {
        val LOCATION_MODE = stringPreferencesKey("location_mode")
        val MAP_LATITUDE = doublePreferencesKey("map_latitude")
        val MAP_LONGITUDE = doublePreferencesKey("map_longitude")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val locationMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LOCATION_MODE] ?: Constants.DefaultSettings.LOCATION_MODE
    }

    suspend fun setLocationMode(mode: String) {
        context.dataStore.edit { it[Keys.LOCATION_MODE] = mode }
    }

    val mapLatitude: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.MAP_LATITUDE] ?: Constants.DefaultSettings.LATITUDE
    }

    val mapLongitude: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.MAP_LONGITUDE] ?: Constants.DefaultSettings.LONGITUDE
    }

    suspend fun setMapLocation(lat: Double, lon: Double) {
        context.dataStore.edit {
            it[Keys.MAP_LATITUDE] = lat
            it[Keys.MAP_LONGITUDE] = lon
        }
    }

    val tempUnit: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.TEMP_UNIT] ?: Constants.DefaultSettings.TEMP_UNIT
    }

    suspend fun setTempUnit(unit: String) {
        context.dataStore.edit { it[Keys.TEMP_UNIT] = unit }
    }

    val windUnit: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.WIND_UNIT] ?: Constants.DefaultSettings.WIND_UNIT
    }

    suspend fun setWindUnit(unit: String) {
        context.dataStore.edit { it[Keys.WIND_UNIT] = unit }
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: Constants.DefaultSettings.LANGUAGE
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }
}

