package com.aeinae.climatrack.data.remote.dto.forecast

import com.aeinae.climatrack.data.remote.dto.common.CoordDto
import com.google.gson.annotations.SerializedName

data class CityDto(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("coord")
    val coord: CoordDto?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("population")
    val population: Int? = null,

    @SerializedName("timezone")
    val timezone: Int?,

    @SerializedName("sunrise")
    val sunrise: Long?,

    @SerializedName("sunset")
    val sunset: Long?,
)