package com.aeinae.climatrack.data.remote.dto.common

import com.google.gson.annotations.SerializedName

data class CloudsDto(
    @SerializedName("all")
    val all: Int,
)