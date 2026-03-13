package com.aeinae.climatrack.data.source.local

import android.annotation.SuppressLint
import com.aeinae.climatrack.data.local.database.dao.WeatherCacheDao
import com.aeinae.climatrack.data.local.database.entity.WeatherCacheEntity
import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.google.gson.Gson

class WeatherLocalDataSource(private val weatherDao: WeatherCacheDao, private val gson: Gson)  {

    @SuppressLint("SuspiciousIndentation")
    suspend fun cacheWeather(key: String, current: CurrentWeatherResponse, forecast: ForecastResponse, lat: Double, lon: Double){
        val weatherCacheEntity = WeatherCacheEntity(
            locationKey = key,
            cityName = current.name?:"Unknown",
            country = current.sys?.country?:"Unknown",
            latitude = lat,
            longitude = lon,
            currentWeather = gson.toJson(current),
            hourlyForecast = gson.toJson(forecast.list),
            dailyForecast = gson.toJson(forecast),
            lastUpdated = System.currentTimeMillis()
        )
            weatherDao.insertWeather(weatherCacheEntity)
    }

    suspend fun getCachedWeather(key: String): CachedWeather?{
            val weatherCacheEntity = weatherDao.getCachedWeatherData(key)?: return null
            return try {
                CachedWeather(
                    current = gson.fromJson(weatherCacheEntity.currentWeather, CurrentWeatherResponse::class.java),
                    forecast = gson.fromJson(weatherCacheEntity.dailyForecast, ForecastResponse::class.java),
                    lastUpdated = weatherCacheEntity.lastUpdated
                )
            }catch (e: Exception){
                weatherDao.deleteByKey(key)
                null
            }
    }

    suspend fun deleteCachedWeather(key: String){
        weatherDao.deleteByKey(key)
    }

    suspend fun getAllCachedWeather(): List<WeatherCacheEntity>{
        return weatherDao.getAll()
    }

    suspend fun clearAllCachedWeather(){
        weatherDao.clearAll()
    }
}