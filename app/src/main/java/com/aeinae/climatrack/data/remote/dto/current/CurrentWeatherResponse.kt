package com.aeinae.climatrack.data.remote.dto.current

import com.aeinae.climatrack.data.remote.dto.common.CloudsDto
import com.aeinae.climatrack.data.remote.dto.common.CoordDto
import com.aeinae.climatrack.data.remote.dto.common.MainDto
import com.aeinae.climatrack.data.remote.dto.common.WeatherConditionDto
import com.aeinae.climatrack.data.remote.dto.common.WindDto
import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @SerializedName("coord")
    val coord: CoordDto,

    @SerializedName("weather")
    val weather: List<WeatherConditionDto>,

    @SerializedName("base")
    val base: String? = null,

    @SerializedName("main")
    val main: MainDto,

    @SerializedName("visibility")
    val visibility: Int,

    @SerializedName("wind")
    val wind: WindDto,

    @SerializedName("clouds")
    val clouds: CloudsDto,

    @SerializedName("dt")
    val dt: Long,

    @SerializedName("sys")
    val sys: CurrentSysDto,

    @SerializedName("timezone")
    val timezone: Int,

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("cod")
    val cod: Int,
)