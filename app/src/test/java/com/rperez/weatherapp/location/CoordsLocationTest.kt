//package com.rperez.weatherapp.location
//
//import android.Manifest.permission.ACCESS_FINE_LOCATION
//import android.content.Context
//import androidx.activity.ComponentActivity
//import androidx.activity.result.ActivityResultLauncher
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import com.rperez.weatherapp.network.model.Main
//import com.rperez.weatherapp.network.model.Weather
//import com.rperez.weatherapp.network.model.WeatherResponse
//import com.rperez.weatherapp.repository.WeatherRepository
//import com.rperez.weatherapp.service.LocationService
//import com.rperez.weatherapp.viewmodel.WeatherViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestCoroutineScheduler
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.MockitoAnnotations
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import org.mockito.kotlin.whenever
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.jvm.isAccessible
//import kotlin.reflect.jvm.javaField
//import kotlin.test.assertFalse
//
//@ExperimentalCoroutinesApi
//class CoordsLocationTest {
//
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    val testScheduler = TestCoroutineScheduler()
//    val testDispatcher = StandardTestDispatcher(testScheduler)
//
//    @Mock
//    lateinit var weatherViewModel: WeatherViewModel
//
//    @Mock
//    lateinit var repository: WeatherRepository
//
//    @Mock
//    lateinit var context: Context
//
//    @Mock
//    private lateinit var activity: ComponentActivity
//
//    @Mock
//    private lateinit var launcher: ActivityResultLauncher<String>
//
//    @Mock
//    private lateinit var locationService : LocationService
//
//    private lateinit var closeable: AutoCloseable
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        closeable = MockitoAnnotations.openMocks(this);
//        activity = mock()
//        context = mock()
//        repository = mock()
//        launcher = mock()
//        locationService = mock()
//        weatherViewModel = mock()
//    }
//
//    @After
//    fun close() {
//        closeable.close()
//    }
//
//    @Test
//    fun `test getLocalWeather requests permission if coords not initialized`() = runTest {
//        //  return
//        weatherViewModel = WeatherViewModel(repository)
//        /*val coordsProperty = weatherViewModel::class.declaredMemberProperties.firstOrNull { it.name == "coords" }
//        coordsProperty?.isAccessible = true
//        val field = coordsProperty?.javaField  // Access the Java field backing this property
//        field?.isAccessible = true
//        field?.set(weatherViewModel, null)
//*/
//        weatherViewModel.setRequestLocationPermissionLauncher(launcher)
//        weatherViewModel.getLocalWeather(context)
//
//        // Verify that the permission request was launched
//        verify(launcher).launch(ACCESS_FINE_LOCATION)
//    }
//}
package com.rperez.weatherapp.location

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rperez.weatherapp.R
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: WeatherRepository

    @Mock
    private lateinit var launcher: ActivityResultLauncher<String>

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var locationService: LocationService // Mock LocationService

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        viewModel = WeatherViewModel(repository)
        viewModel.setRequestLocationPermissionLauncher(launcher)
        viewModel.setupLocationService(locationService)
    }

    @Test
    fun `getLocalWeather should launch permission dialog when coords not initialized`() = runTest {
        // Given coords is not initialized
        `when`(context.getString(R.string.location_permission_needed)).thenReturn("Location permission needed")
        `when`(context.getString(R.string.this_app_needs_the_location_permission_to_fetch_weather_data_for_your_current_location))
            .thenReturn("This app needs the location permission to fetch weather data for your current location")
        `when`(context.getString(R.string.grant)).thenReturn("Grant")
        `when`(context.getString(R.string.cancel)).thenReturn("Cancel")

        // Simulate permission request via LocationService mock
        doAnswer {
            val onPermissionRequired = it.getArgument<() -> Unit>(1)
            onPermissionRequired.invoke() // Trigger the permission request callback
        }.`when`(locationService).getLatLon(any(), any())

        // Call the getLocalWeather method, which should trigger permission handling due to uninitialized coords
        viewModel.getLocalWeather(context)

        // Verify the launcher is called to request permission
        verify(launcher).launch(ACCESS_FINE_LOCATION)
    }
}
