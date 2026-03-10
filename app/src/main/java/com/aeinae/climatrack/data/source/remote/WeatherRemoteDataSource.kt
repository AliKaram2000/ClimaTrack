package com.aeinae.climatrack.data.source.remote

import com.aeinae.climatrack.data.remote.api.OpenWeatherMapService
import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.aeinae.climatrack.utils.Constants.API_KEY
import java.io.IOException

class WeatherRemoteDataSource(private val service: OpenWeatherMapService): BaseRemoteDataSource() {

    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String? = null, lang: String? = null): Result<CurrentWeatherResponse>{
        return safeApiCall {
            val response = service.getCurrentWeather(lat, lon, API_KEY, units, lang)
            if (!response.isSuccessful) throw IOException("Request failed with code ${response.code()}")
            response.body() ?: throw IOException("Response body is null")
        }
        /*return try{
            val response = service.getCurrentWeather(lat, lon, API_KEY, units, lang)
            if(response.isSuccessful && response.body()!=null){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception("Request failed with code ${response.code()}"))
            }
        }catch (e: IOException){
            Result.failure(e)
        }*/
    }

    suspend fun getForecast(lat: Double, lon: Double, units: String? = null, lang: String? = null): Result<ForecastResponse>{
        return safeApiCall {
            val response = service.getForecast(lat, lon, API_KEY, units, lang)
            if (!response.isSuccessful) throw IOException("Request failed with code ${response.code()}")
            response.body() ?: throw IOException("Response body is null")
        }
    }
}


