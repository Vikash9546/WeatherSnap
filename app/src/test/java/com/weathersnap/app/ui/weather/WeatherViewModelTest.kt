package com.weathersnap.app.ui.weather

import app.cash.turbine.test
import com.weathersnap.app.domain.model.GeocodingResult
import com.weathersnap.app.domain.model.WeatherData
import com.weathersnap.app.domain.usecase.GetWeatherUseCase
import com.weathersnap.app.domain.usecase.SearchCityUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private val searchCityUseCase: SearchCityUseCase = mockk(relaxed = true)
    private val getWeatherUseCase: GetWeatherUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(searchCityUseCase, getWeatherUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        assertEquals(WeatherUiState.Idle, viewModel.weatherState.value)
    }

    @Test
    fun `performSearch updates weather state correctly`() = runTest {
        // Given
        val query = "London"
        val location = GeocodingResult(1L, query, 51.5, -0.12, "UK", "London")
        val weather = WeatherData(query, 20.0, "Sunny", 60, 5.0, 1013.0, 0)

        coEvery { searchCityUseCase(any()) } returns Result.success(listOf(location))
        coEvery { getWeatherUseCase(any()) } returns Result.success(weather)

        // When
        viewModel.onQueryChanged(query)
        testDispatcher.scheduler.advanceTimeBy(500) // for debounce
        viewModel.performSearch()
        testDispatcher.scheduler.advanceUntilIdle() // Wait for internal coroutines

        // Then
        assertTrue(viewModel.weatherState.value is WeatherUiState.Success)
        assertEquals(weather, (viewModel.weatherState.value as WeatherUiState.Success).weather)
    }
}
