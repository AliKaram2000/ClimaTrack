package com.aeinae.climatrack.data.source.remote

import com.aeinae.climatrack.data.remote.api.OpenWeatherMapService
import com.aeinae.climatrack.data.remote.dto.geocoding.GeocodingResponseDto
import com.aeinae.climatrack.utils.Constants.API_KEY
import com.aeinae.climatrack.utils.Constants.REVERSE_GEOCODING_LIMIT
import com.aeinae.climatrack.utils.Constants.TOP_MATCHES
import java.io.IOException

class GeocodingRemoteDataSource(private val service: OpenWeatherMapService): BaseRemoteDataSource() {

    suspend fun getCity(query: String): Result<List<GeocodingResponseDto>>{
        return safeApiCall {
            val response = service.getCity(query,TOP_MATCHES ,API_KEY)
            if (!response.isSuccessful) throw IOException("Request failed with code ${response.code()}")
            response.body() ?: throw IOException("Response body is null")
        }
        /*
         runCatching {
            service.getCity(query,TOP_MATCHES ,API_KEY)
        }.mapCatching { response ->
            if (!response.isSuccessful) throw IOException("Request failed with code ${response.code()}")
            response.body() ?: throw IOException("Response body is null")
        }
        * */
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): Result<List<GeocodingResponseDto>>{
        return safeApiCall {
            val response = service.reverseGeocode(lat, lon, REVERSE_GEOCODING_LIMIT, API_KEY)
            if (!response.isSuccessful) throw IOException("Request failed with code ${response.code()}")
            response.body() ?: throw IOException("Response body is null")
        }
    }
}