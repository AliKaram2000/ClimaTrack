package com.aeinae.climatrack.data.remote.dto.forecast

import com.aeinae.climatrack.data.remote.dto.common.CloudsDto
import com.aeinae.climatrack.data.remote.dto.common.MainDto
import com.aeinae.climatrack.data.remote.dto.common.WeatherConditionDto
import com.aeinae.climatrack.data.remote.dto.common.WindDto
import com.google.gson.annotations.SerializedName

data class ForecastItemDto(
    @SerializedName("dt")
    val dt: Long,

    @SerializedName("main")
    val main: MainDto?,

    @SerializedName("weather")
    val weather: List<WeatherConditionDto>?,

    @SerializedName("clouds")
    val clouds: CloudsDto?,

    @SerializedName("wind")
    val wind: WindDto?,

    @SerializedName("visibility")
    val visibility: Int?,

    @SerializedName("pop")
    val pop: Double?,

    @SerializedName("sys")
    val sys: ForecastSysDto?,

    @SerializedName("dt_txt")
    val dtTxt: String?,
)