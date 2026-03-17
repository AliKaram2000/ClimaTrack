package com.aeinae.climatrack.data.source.local

import com.aeinae.climatrack.data.local.database.dao.WeatherCacheDao
import com.aeinae.climatrack.data.local.database.entity.WeatherCacheEntity
import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WeatherLocalDataSourceTest {

    private lateinit var dao: WeatherCacheDao
    private lateinit var gson: Gson
    private lateinit var localDataSource: WeatherLocalDataSource

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        gson = Gson()
        localDataSource = WeatherLocalDataSource(dao, gson)
    }

    // ── Test Data ──

    private fun createCurrentWeatherResponse(): CurrentWeatherResponse {
        val json = """
        {
            "name": "Cairo",
            "dt": 1700000000,
            "main": { "temp": 25.0, "feels_like": 23.0, "humidity": 50, "pressure": 1013, "temp_min": 22.0, "temp_max": 28.0 },
            "weather": [{ "description": "clear sky", "icon": "01d" }],
            "wind": { "speed": 3.5 },
            "clouds": { "all": 10 },
            "sys": { "country": "EG" }
        }
        """.trimIndent()
        return gson.fromJson(json, CurrentWeatherResponse::class.java)
    }

    private fun createForecastResponse(): ForecastResponse {
        val json = """
        {
            "list": [
                {
                    "dt": 1700000000,
                    "main": { "temp": 25.0, "feels_like": 23.0, "humidity": 50, "pressure": 1013, "temp_min": 22.0, "temp_max": 28.0 },
                    "weather": [{ "description": "clear sky", "icon": "01d" }],
                    "wind": { "speed": 3.5 },
                    "clouds": { "all": 10 }
                }
            ],
            "city": { "name": "Cairo", "country": "EG" }
        }
        """.trimIndent()
        return gson.fromJson(json, ForecastResponse::class.java)
    }

    private fun createCacheEntity(key: String): WeatherCacheEntity {
        val current = createCurrentWeatherResponse()
        val forecast = createForecastResponse()
        return WeatherCacheEntity(
            locationKey = key,
            cityName = "Cairo",
            country = "EG",
            latitude = 30.0444,
            longitude = 31.2357,
            currentWeather = gson.toJson(current),
            hourlyForecast = gson.toJson(forecast.list),
            dailyForecast = gson.toJson(forecast),
            lastUpdated = 1700000000L
        )
    }


    @Test
    fun cacheWeather_insertsEntityWithCorrectKey() = runTest {
        val current = createCurrentWeatherResponse()
        val forecast = createForecastResponse()
        val entitySlot = slot<WeatherCacheEntity>()

        coEvery { dao.insertWeather(capture(entitySlot)) } returns Unit

        localDataSource.cacheWeather("home", current, forecast, 30.0444, 31.2357)

        coVerify(exactly = 1) { dao.insertWeather(any()) }
        assertEquals("home", entitySlot.captured.locationKey)
        assertEquals("Cairo", entitySlot.captured.cityName)
        assertEquals("EG", entitySlot.captured.country)
    }

    @Test
    fun getCachedWeather_existingKey_returnsData() = runTest {
        val entity = createCacheEntity("home")
        coEvery { dao.getCachedWeatherData("home") } returns entity

        val result = localDataSource.getCachedWeather("home")

        assertNotNull(result)
        assertEquals("Cairo", result!!.current.name)
        assertEquals(1700000000L, result.lastUpdated)
    }

    @Test
    fun getCachedWeather_nonExistingKey_returnsNull() = runTest {
        coEvery { dao.getCachedWeatherData("missing") } returns null

        val result = localDataSource.getCachedWeather("missing")

        assertNull(result)
    }

    @Test
    fun getCachedWeather_corruptedJson_returnsNullAndDeletesCache() = runTest {
        val corruptEntity = WeatherCacheEntity(
            locationKey = "corrupt",
            cityName = "Bad",
            country = "XX",
            latitude = 0.0,
            longitude = 0.0,
            currentWeather = "not valid json{{{",
            hourlyForecast = "not valid json{{{",
            dailyForecast = "not valid json{{{",
            lastUpdated = 1700000000L
        )
        coEvery { dao.getCachedWeatherData("corrupt") } returns corruptEntity

        val result = localDataSource.getCachedWeather("corrupt")

        assertNull(result)
        coVerify(exactly = 1) { dao.deleteByKey("corrupt") }
    }

    @Test
    fun clearAllCachedWeather_callsDaoClearAll() = runTest {
        localDataSource.clearAllCachedWeather()

        coVerify(exactly = 1) { dao.clearAll() }
    }

    @Test
    fun deleteCachedWeather_callsDaoDeleteByKey() = runTest {
        localDataSource.deleteCachedWeather("fav_1")

        coVerify(exactly = 1) { dao.deleteByKey("fav_1") }
    }
}