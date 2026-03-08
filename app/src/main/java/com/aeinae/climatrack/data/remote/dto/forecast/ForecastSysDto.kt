package com.aeinae.climatrack.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName

data class ForecastSysDto(
    @SerializedName("pod")
    val pod: String,
)