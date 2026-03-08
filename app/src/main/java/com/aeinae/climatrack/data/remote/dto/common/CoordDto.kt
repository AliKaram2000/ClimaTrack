package com.aeinae.climatrack.data.remote.dto.common

import com.google.gson.annotations.SerializedName

data class CoordDto(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
)