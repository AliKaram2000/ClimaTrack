package com.aeinae.climatrack.views.home

import com.aeinae.climatrack.data.location.LocationService
import com.aeinae.climatrack.data.remote.dto.current.CurrentWeatherResponse
import com.aeinae.climatrack.data.remote.dto.forecast.ForecastResponse
import com.aeinae.climatrack.data.repository.SettingsRepository
import com.aeinae.climatrack.data.repository.WeatherRepository
import com.aeinae.climatrack.domain.results.WeatherResult
import com.aeinae.climatrack.utils.Constants
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var locationService: LocationService
    private val gson = Gson()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        weatherRepository = mockk()
        locationService = mockk()
        settingsRepository = mockk()

        // Default settings — map mode to avoid GPS permission issues in tests
        every { settingsRepository.locationModeFlow } returns flowOf(Constants.LocationMode.MAP)
        every { settingsRepository.mapLatitudeFlow } returns flowOf(30.0444)
        every { settingsRepository.mapLongitudeFlow } returns flowOf(31.2357)
        every { settingsRepository.tempUnitFlow } returns flowOf(Constants.Units.METRIC)
        every { settingsRepository.windUnitFlow } returns flowOf(Constants.WindUnits.METER_SEC)
        every { settingsRepository.languageFlow } returns flowOf(Constants.Languages.ENGLISH)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
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


    @Test
    fun initialState_isLoading() = runTest {
        coEvery { weatherRepository.getWeather(any(), any(), any(), any(), any()) } returns
                WeatherResult.Success(
                    current = createCurrentWeatherResponse(),
                    forecast = createForecastResponse(),
                    isFromCache = false,
                    lastUpdated = System.currentTimeMillis()
                )

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun successfulFetch_stateBecomesSuccess() = runTest {
        coEvery { weatherRepository.getWeather(any(), any(), any(), any(), any()) } returns
                WeatherResult.Success(
                    current = createCurrentWeatherResponse(),
                    forecast = createForecastResponse(),
                    isFromCache = false,
                    lastUpdated = 1700000000L
                )

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)

        val success = state as HomeUiState.Success
        assertEquals("Cairo", success.current.cityName)
        assertEquals("EG", success.current.country)
        assertEquals(25, success.current.temperature.toInt())
        assertEquals(Constants.Units.METRIC, success.tempUnit)
        assertEquals(Constants.WindUnits.METER_SEC, success.windUnit)
        assertEquals(false, success.isFromCache)
    }

    @Test
    fun failedFetch_stateBecomesError() = runTest {
        coEvery { weatherRepository.getWeather(any(), any(), any(), any(), any()) } returns
                WeatherResult.Error(message = "Unable to load data")

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals("Unable to load data", (state as HomeUiState.Error).message)
    }

    @Test
    fun cachedFetch_stateShowsFromCache() = runTest {
        coEvery { weatherRepository.getWeather(any(), any(), any(), any(), any()) } returns
                WeatherResult.Success(
                    current = createCurrentWeatherResponse(),
                    forecast = createForecastResponse(),
                    isFromCache = true,
                    lastUpdated = 1700000000L
                )

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(true, (state as HomeUiState.Success).isFromCache)
    }

    @Test
    fun gpsMode_locationFailure_stateBecomesError() = runTest {
        every { settingsRepository.locationModeFlow } returns flowOf(Constants.LocationMode.GPS)
        coEvery { locationService.getCurrentLocation() } returns
                Result.failure(SecurityException("Location permission not granted"))

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals("Location permission not granted", (state as HomeUiState.Error).message)
    }

    @Test
    fun requiresGpsPermission_mapMode_returnsFalse() = runTest {
        coEvery { weatherRepository.getWeather(any(), any(), any(), any(), any()) } returns
                WeatherResult.Success(
                    current = createCurrentWeatherResponse(),
                    forecast = createForecastResponse(),
                    isFromCache = false,
                    lastUpdated = System.currentTimeMillis()
                )

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)
        advanceUntilIdle()

        assertEquals(false, viewModel.requiresGpsPermission())
    }

    @Test
    fun refresh_updatesStateWithoutLoading() = runTest {
        coEvery { weatherRepository.getWeather(any(), any(), any(), any(), any()) } returns
                WeatherResult.Success(
                    current = createCurrentWeatherResponse(),
                    forecast = createForecastResponse(),
                    isFromCache = false,
                    lastUpdated = 1700000000L
                )

        val viewModel = HomeViewModel(weatherRepository, settingsRepository, locationService)
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Success)
        assertEquals(false, viewModel.isRefreshing.value)
    }
}