package com.aeinae.climatrack.data.repository

import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.aeinae.climatrack.data.source.local.WeatherLocalDataSource
import com.aeinae.climatrack.data.source.remote.WeatherRemoteDataSource
import com.aeinae.climatrack.domain.results.WeatherResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class WeatherRepository(private val remoteDataSource: WeatherRemoteDataSource,
                        private val localDataSource: WeatherLocalDataSource) {
    /*************************************************************************************/
    /*********************************|UTILITY FUNCTIONS|*********************************/
    /*************************************************************************************/
    private suspend fun fetchFromNetwork(
        lat: Double,
        lon: Double,
        units: String? = null,
        language: String? = null,
    ): Result<Pair<CurrentWeatherResponse, ForecastResponse>> {
        return try {
            coroutineScope {
                val currentDeferred = async {
                    remoteDataSource.getCurrentWeather(lat, lon, units, language)
                }
                val forecastDeferred = async {
                    remoteDataSource.getForecast(lat, lon, units, language)
                }

                val currentResult = currentDeferred.await()
                val forecastResult = forecastDeferred.await()

                if (currentResult.isSuccess && forecastResult.isSuccess) {
                    Result.success(
                        Pair(
                            currentResult.getOrThrow(),
                            forecastResult.getOrThrow(),
                        )
                    )
                } else {
                    val error = currentResult.exceptionOrNull()
                        ?: forecastResult.exceptionOrNull()
                        ?: Exception("Unknown API error")
                    Result.failure(error)
                }
            }
        } catch (e: CancellationException){
            throw e
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun fallbackToCache(key: String): WeatherResult{
       return localDataSource.getCachedWeather(key)?.let { cachedWeather->
           WeatherResult.Success(
               current = cachedWeather.current,
               forecast = cachedWeather.forecast,
               isFromCache = true,
               lastUpdated = cachedWeather.lastUpdated
           )
       }?: WeatherResult.Error(message = "Unable to load data")
    }
    /*************************************************************************************/
    /*************************************************************************************/
    suspend fun getWeather(lat: Double, lon: Double, units: String? = null, lang: String? = null, key: String): WeatherResult {
        return try {
            val networkResult = fetchFromNetwork(lat, lon, units, lang)

            if (networkResult.isSuccess) {
                val (current, forecast) = networkResult.getOrThrow()

                localDataSource.cacheWeather(key, current, forecast, lat, lon)

                WeatherResult.Success(
                    current = current,
                    forecast = forecast,
                    isFromCache = false,
                    lastUpdated = System.currentTimeMillis(),
                )
            } else {
                fallbackToCache(key)
        }
    }catch (e: CancellationException){
        throw e
    }catch (e: Exception){
            WeatherResult.Error(message = e.message ?: "Unable to load data")
        }
    }
    suspend fun clearAllCache(){
        localDataSource.clearAllCachedWeather()
    }
    suspend fun clearCache(key: String){
        localDataSource.deleteCachedWeather(key)
    }
}