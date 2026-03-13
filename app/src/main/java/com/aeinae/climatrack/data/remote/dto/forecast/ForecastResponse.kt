package com.aeinae.climatrack.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("cod")
    val cod: String?,

    @SerializedName("message")
    val message: Int?,

    @SerializedName("cnt")
    val cnt: Int?,

    @SerializedName("list")
    val list: List<ForecastItemDto>?,

    @SerializedName("city")
    val city: CityDto?,
)