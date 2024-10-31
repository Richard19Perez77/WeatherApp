package com.rperez.weatherapp.viewmodel

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.network.model.Main
import com.rperez.weatherapp.network.model.Weather
import com.rperez.weatherapp.network.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.time.LocalDate import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
class WeatherViewModelUITest {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var temperatureViewModel: TemperatureViewModel
    private lateinit var mockRepository: WeatherRepository
    private lateinit var mockContext: Context
    private lateinit var mockLauncher: ActivityResultLauncher<String>
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        mockRepository = mock()
        mockContext = mock()
        mockLauncher = mock()
        temperatureViewModel = mock()

        Dispatchers.setMain(testDispatcher) // Use the test dispatcher

        weatherViewModel = WeatherViewModel(mockRepository).apply {
            setRequestLocationPermissionLauncher(mockLauncher)
            setupWeatherObserver(temperatureViewModel::insertTemperature)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getWeather - updates UI state on success`() = runTest {
        // Given
        val cityName = "Tokyo"
        weatherViewModel.setCityName(cityName)
        val weatherResponse = WeatherResponse(
            main = Main(temp = 20.0, humidity = 50, pressure = 1010),
            weather = listOf(Weather(description = "Clear Sky", icon = "01d")),
            name = cityName
        )

        whenever(mockRepository.getWeatherByCityData(mockContext, {""}, cityName)).thenReturn(Result.success(weatherResponse))

        // When
        weatherViewModel.getWeather(mockContext)

        // Assert loading state before advancing dispatcher
        assertTrue("Expected loading state to be true", weatherViewModel.uiState.value.isLoading)

        // Advance until the coroutine completes
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert final state after coroutine completes
        assertEquals(cityName, weatherViewModel.uiState.value.name)
        assertEquals(20.0, weatherViewModel.uiState.value.temperature, 0.01)
        assertEquals("Clear Sky", weatherViewModel.uiState.value.description)
        assertFalse("Expected loading state to be false after fetch", weatherViewModel.uiState.value.isLoading)
        assertTrue("Expected no error message", weatherViewModel.uiState.value.errorMessage.isEmpty())
    }

    @Test
    fun `getWeather - updates UI state on failure`() = runTest {
        // Given
        val cityName = "UnknownCity"
        val errorMessage = "Network Error"
        whenever(mockRepository.getWeatherByCityData(mockContext, {""}, cityName)).thenReturn(Result.failure(Exception(errorMessage)))
        weatherViewModel.setCityName(cityName)

        // When
        weatherViewModel.getWeather(mockContext)

        // Check initial loading state
        assertTrue("Expected loading state to be true initially", weatherViewModel.uiState.value.isLoading)

        // Advance dispatcher to process the failure response
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse("Expected loading state to be false after error", weatherViewModel.uiState.value.isLoading)
        assertEquals(errorMessage, weatherViewModel.uiState.value.errorMessage)
        assertEquals(cityName, weatherViewModel.uiState.value.name)
    }

    @Test
    fun `setupWeatherObserver - inserts temperature data on success`() = runTest {
        // Setup the date and time for consistent test results
        var testDate = LocalDate.now()
        val temperatureEntity = TemperatureEntity(
            id = 0,
            date = testDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            city = "Tokyo",
            desc = "Clear Sky",
            temperature = 20.0,
            humidity = 50,
            pressure = 1010,
        )

        // Mock repository response
        val weatherResponse = WeatherResponse(
            main = Main(temp = 20.0, humidity = 50, pressure = 1010),
            weather = listOf(Weather(description = "Clear Sky", icon = "01d")),
            name = "Tokyo"
        )
        whenever(mockRepository.getWeatherByCityData(mockContext, {""}, "Tokyo")).thenReturn(Result.success(weatherResponse))

        // Act
        weatherViewModel.setCityName("Tokyo")
        weatherViewModel.getWeather(mockContext)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify insertTemperature was called with the expected entity
        verify(temperatureViewModel).insertTemperature(argThat {
            this.date == temperatureEntity.date &&
                    this.city == temperatureEntity.city &&
                    this.desc == temperatureEntity.desc &&
                    this.temperature == temperatureEntity.temperature &&
                    this.humidity == temperatureEntity.humidity &&
                    this.pressure == temperatureEntity.pressure
        })
    }
}