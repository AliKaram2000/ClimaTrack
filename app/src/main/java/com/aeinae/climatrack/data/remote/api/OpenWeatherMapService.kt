package com.aeinae.climatrack.data.remote.api

import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.aeinae.climatrack.data.remote.dto.geocoding.GeocodingResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String? = null,
        @Query("lang") language: String? = null,
        ): Response<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String? = null,
        @Query("lang") language: String? = null,
    ): Response<ForecastResponse>

    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String,
    ): Response<List<GeocodingResponseDto>>

    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String,
    ): Response<List<GeocodingResponseDto>>
}



