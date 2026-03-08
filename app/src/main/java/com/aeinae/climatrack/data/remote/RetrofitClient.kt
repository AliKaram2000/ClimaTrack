package com.aeinae.climatrack.data.remote

import com.aeinae.climatrack.data.remote.api.OpenWeatherMapService
import com.aeinae.climatrack.utils.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitClient {

    val retrofitClient = Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
    val service = retrofitClient.create<OpenWeatherMapService> (OpenWeatherMapService::class.java)
}