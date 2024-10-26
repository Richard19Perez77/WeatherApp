package com.rperez.weatherapp.location

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rperez.weatherapp.network.model.Main
import com.rperez.weatherapp.network.model.Weather
import com.rperez.weatherapp.network.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
class CoordsTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)

    lateinit var weatherViewModel: WeatherViewModel

    @Mock
    lateinit var repository: WeatherRepository

    @Mock
    lateinit var context: Context

    @Mock
    private lateinit var activity: ComponentActivity

    @Mock
    private lateinit var launcher: ActivityResultLauncher<String>

    val cityName = "Tokyo"
    val weatherResponse = WeatherResponse(
        main = Main(temp = 20.0, humidity = 50, pressure = 1010),
        weather = listOf(Weather(description = "Clear Sky", icon = "01d")),
        name = cityName
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        activity = mock()
        context = mock()
        repository = mock()
        launcher = mock()
        weatherViewModel = WeatherViewModel(repository)
    }

    @Test
    fun `test coords initialized after receiving location`() = runTest {
        // Simulate receiving location
        val lat = 35.0
        val lon = 139.0
        weatherViewModel.coords = Pair(lat, lon)

        assert(weatherViewModel.coords.first == lat)
        assert(weatherViewModel.coords.second == lon)
    }

    @Test
    fun `test getLocalWeather uses initialized coords`() = runTest {
        weatherViewModel.setRequestLocationPermissionLauncher(launcher)

        val lat = 35.0
        val lon = 139.0

        // Initialize coords
        weatherViewModel.coords = Pair(lat, lon)

        // Mock the repository's response
        whenever(repository.getWeatherGeoData(lat, lon)).thenReturn(Result.success(weatherResponse))

        weatherViewModel.getLocalWeather(context)

        // assert call starts loading coords from get local weather and completes
        assert(weatherViewModel.uiState.value.isLoading)
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(weatherViewModel.uiState.value.isLoading)
        testDispatcher.scheduler.advanceUntilIdle()
    }
}
