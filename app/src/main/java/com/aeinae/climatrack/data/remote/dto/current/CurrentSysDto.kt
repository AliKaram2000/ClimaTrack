package com.aeinae.climatrack.data.remote.dto.current

import com.google.gson.annotations.SerializedName

data class CurrentSysDto(
    @SerializedName("type")
    val type: Int? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("country")
    val country: String,

    @SerializedName("sunrise")
    val sunrise: Long,

    @SerializedName("sunset")
    val sunset: Long,
)