package com.aeinae.climatrack.domain.mappers

import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastItemDto
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.aeinae.climatrack.domain.models.CurrentWeather
import com.aeinae.climatrack.domain.models.DailyForecast
import com.aeinae.climatrack.domain.models.HourlyForecast
import java.util.Calendar
import kotlin.math.abs

object WeatherMapper {

    fun mapCurrentWeather(dto: CurrentWeatherResponse): CurrentWeather {
        val firstWeather = dto.weather?.firstOrNull()

        return CurrentWeather(
            cityName = dto.name ?: "Unknown",
            country = dto.sys?.country ?: "--",
            temperature = dto.main?.temp ?: 0.0,
            feelsLike = dto.main?.feelsLike ?: 0.0,
            description = firstWeather?.description ?: "No description",
            iconCode = firstWeather?.icon ?: "01d",
            humidity = dto.main?.humidity ?: 0,
            pressure = dto.main?.pressure ?: 0,
            windSpeed = dto.wind?.speed ?: 0.0,
            cloudiness = dto.clouds?.all ?: 0,
            dateTime = dto.dt ?: 0L
        )
    }

    fun mapHourlyForecasts(dto: ForecastResponse): List<HourlyForecast> {
        val items = dto.list ?: return emptyList()

        return items.take(8).map { item ->
            val firstWeather = item.weather?.firstOrNull()

            HourlyForecast(
                dateTime = item.dt,
                temperature = item.main?.temp ?: 0.0,
                iconCode = firstWeather?.icon ?: "01d",
                description = firstWeather?.description ?: "No description"
            )
        }
    }

    fun mapDailyForecasts(dto: ForecastResponse): List<DailyForecast> {
        val items = dto.list ?: return emptyList()

        return items
            .groupBy { item -> dayKey(item.dt) }
            .entries
            .sortedBy { entry -> entry.value.first().dt }
            .map { (_, dayItems) -> aggregateDay(dayItems) }
    }


    private fun dayKey(dtSeconds: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dtSeconds * 1000
        }
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_YEAR)
        return "$year-$day"
    }


    private fun aggregateDay(dayItems: List<ForecastItemDto>): DailyForecast {
        val tempMin = dayItems.mapNotNull { it.main?.tempMin }.minOrNull() ?: 0.0
        val tempMax = dayItems.mapNotNull { it.main?.tempMax }.maxOrNull() ?: 0.0

        val middayItem = dayItems.minByOrNull { item ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = item.dt * 1000
            }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            abs(hour - 12)
        }

        val middayWeather = middayItem?.weather?.firstOrNull()

        return DailyForecast(
            dateTime = middayItem?.dt ?: dayItems.first().dt,
            tempMin = tempMin,
            tempMax = tempMax,
            iconCode = middayWeather?.icon ?: "01d",
            description = middayWeather?.description ?: "No description"
        )
    }
}