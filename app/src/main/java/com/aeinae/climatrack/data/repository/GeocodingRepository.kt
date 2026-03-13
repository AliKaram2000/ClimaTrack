package com.aeinae.climatrack.data.repository

import com.aeinae.climatrack.data.remote.dto.geocoding.GeocodingResponseDto
import com.aeinae.climatrack.data.source.remote.GeocodingRemoteDataSource

class GeocodingRepository(private val remoteDataSource: GeocodingRemoteDataSource) {

    suspend fun searchCities(query: String): Result<List<GeocodingResponseDto>> {
        return remoteDataSource.getCity(query)
    }

    suspend fun reverseGeocode(lat: Double, lon: Double): Result<GeocodingResponseDto?> {
        return remoteDataSource.reverseGeocode(lat, lon).map { geoCodeList-> geoCodeList.firstOrNull() }
    }
}