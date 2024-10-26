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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
class CoordsLocationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)

    @Mock
    lateinit var weatherViewModel: WeatherViewModel

    @Mock
    lateinit var repository: WeatherRepository

    @Mock
    lateinit var context: Context

    @Mock
    private lateinit var activity: ComponentActivity

    @Mock
    private lateinit var launcher: ActivityResultLauncher<String>

    @Mock
    private lateinit var locationService : LocationService

    private lateinit var closeable: AutoCloseable

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        closeable = MockitoAnnotations.openMocks(this);
        activity = mock()
        context = mock()
        repository = mock()
        launcher = mock()
        locationService = mock()
        weatherViewModel = mock()
    }

    @After
    fun close() {
        closeable.close()
    }

    @Test
    fun `test getLocalWeather requests permission if coords not initialized`() = runTest {
        //  return
        weatherViewModel = WeatherViewModel(repository)
        weatherViewModel.setRequestLocationPermissionLauncher(launcher)
        weatherViewModel.getLocalWeather(context)

        // Verify that the permission request was launched
        verify(launcher).launch(ACCESS_FINE_LOCATION)
    }
}
