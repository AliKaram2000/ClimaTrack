package com.aeinae.climatrack.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aeinae.climatrack.data.local.database.entity.WeatherCacheEntity
@Dao
interface WeatherCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherCacheEntity)

    @Query("SELECT * FROM weather_cache WHERE locationKey = :locationKey")
    suspend fun getCachedWeatherData(locationKey: String): WeatherCacheEntity?

    @Query("DELETE FROM weather_cache WHERE locationKey = :key")
    suspend fun deleteByKey(key: String)

    @Query("SELECT * FROM weather_cache")
    suspend fun getAll(): List<WeatherCacheEntity>

    @Query("DELETE FROM weather_cache")
    suspend fun clearAll()
}