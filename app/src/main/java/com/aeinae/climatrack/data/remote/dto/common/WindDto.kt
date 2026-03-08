package com.aeinae.climatrack.data.remote.dto.common

import com.google.gson.annotations.SerializedName

data class WindDto(
    @SerializedName("speed")
    val speed: Double,

    @SerializedName("deg")
    val deg: Int,

    @SerializedName("gust")
    val gust: Double? = null,
)