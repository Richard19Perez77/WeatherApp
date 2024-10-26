package com.rperez.weatherapp.viewmodel

import app.cash.turbine.test
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.network.model.Main
import com.rperez.weatherapp.network.model.Weather
import com.rperez.weatherapp.network.model.WeatherResponse
import com.rperez.weatherapp.network.model.WeatherUI
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @Mock
    private lateinit var weatherRepository: WeatherRepository
    @Mock
    private lateinit var temperatureViewModel: TemperatureViewModel
    @Mock
    private lateinit var locationService: LocationService

    private lateinit var weatherViewModel: WeatherViewModel
    private val testDispatcher = StandardTestDispatcher()
    val cityName = "Tokyo"
    val weatherResponse = WeatherResponse(
        main = Main(temp = 20.0, humidity = 50, pressure = 1010),
        weather = listOf(Weather(description = "Clear Sky", icon = "01d")),
        name = cityName
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        weatherViewModel = WeatherViewModel(weatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test insertTemperature is called with new data`() = runTest {
        val expectedTemperatureEntity = TemperatureEntity(
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
            temperature = 20.0,
            city = "Tokyo",
            desc = "Clear Sky",
            humidity = 50,
            pressure = 1010
            // Do not set a timestamp here
        )

        weatherViewModel.setupWeatherObserver(temperatureViewModel::insertTemperature)

        // Use reflection to access the private _uiState property and emit a new state
        val uiStateField = WeatherViewModel::class.java.getDeclaredField("_uiState")
        uiStateField.isAccessible = true
        val uiState = uiStateField.get(weatherViewModel) as MutableStateFlow<WeatherUI>
        uiState.emit(
            WeatherUI(
                isLoading = false,
                temperature = 20.0,
                humidity = 50,
                airPressure = 1010,
                name = "Tokyo",
                description = "Clear Sky",
                errorMessage = ""
            )
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // Capture the argument passed to insertTemperature
        val captor = argumentCaptor<TemperatureEntity>()
        verify(temperatureViewModel).insertTemperature(captor.capture())

        // Get the actual captured entity
        val capturedValue = captor.firstValue

        // Assert the fields, ignoring the timestamp
        assertEquals(expectedTemperatureEntity.date, capturedValue.date)
        assertEquals(expectedTemperatureEntity.temperature, capturedValue.temperature)
        assertEquals(expectedTemperatureEntity.city, capturedValue.city)
        assertEquals(expectedTemperatureEntity.desc, capturedValue.desc)
        assertEquals(expectedTemperatureEntity.humidity, capturedValue.humidity)
        assertEquals(expectedTemperatureEntity.pressure, capturedValue.pressure)
        // Optionally, you could assert that the timestamp is not null or meets certain conditions
        assertNotNull(capturedValue.timeStamp) // Example check
    }

    @Test
    fun `getWeather - successful fetch updates UI state`() = runTest {
        weatherViewModel.setCityName("Tokyo")

        `when`(weatherRepository.getWeatherByCityData(cityName))
            .thenReturn(Result.success(weatherResponse))

        weatherViewModel.getWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        weatherViewModel.uiState.test {
            val item = awaitItem()
            assertEquals(cityName, item.name)
            assertEquals(20.0, item.temperature)
            assertEquals("Clear Sky", item.description)
        }
    }

    @Test
    fun `getWeather - failure updates UI state with error`() = runTest {
        val errorMessage = "City not found"
        `when`(weatherRepository.getWeatherByCityData(cityName))
            .thenReturn(Result.failure(Exception(errorMessage)))

        weatherViewModel.getWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        weatherViewModel.uiState.test {
            val item = awaitItem()
            assertTrue(item.errorMessage.contains(errorMessage))
            assertEquals(cityName, item.name)
        }
    }

    @Test
    fun `setCityName updates cityName state`() {
        val newCityName = "Tokyo"
        weatherViewModel.setCityName(newCityName)
        assertEquals(newCityName, weatherViewModel.getCityName())
    }
}
