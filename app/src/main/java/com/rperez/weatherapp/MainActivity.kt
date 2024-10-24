package com.rperez.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.rperez.weatherapp.ui.navigation.WeatherAppNavHost
import com.rperez.weatherapp.ui.theme.WeatherAppTheme
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main entry point of the WeatherApp. Handles the UI setup and initiates
 * weather-related operations, such as fetching local weather and managing
 * user preferences for the default city.
 *
 * Future Implementations:
 * - Adjust air pressure by city/zip code for normal or unhealthy values.
 * - Implement trend graphs per city (e.g., line graphs of temperature).
 * - Consider moving the ViewModel logic into the composables for better state handling.
 * - Improve API client calls with defensive programming techniques.
 * - Detect small temperature trends between default and local city settings.
 */
class MainActivity : ComponentActivity() {

    /**
     * ViewModel for handling weather data.
     * Responsible for interacting with weather APIs and updating the UI.
     */
    private val weatherViewModel: WeatherViewModel by viewModel()

    /**
     * ViewModel for handling temperature data, responsible for storing temperatures
     * in the local database and observing weather changes.
     */
    private val temperatureViewModel: TemperatureViewModel by viewModel()

    /**
     * Registers a callback for location permissions.
     * If granted, fetches the local weather.
     * If denied, defaults to Tokyo weather as fallback (Tokyo is used as a global default).
     */
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                weatherViewModel.getLocalWeather(context = this.applicationContext)
            } else {
                weatherViewModel.getWeather("Tokyo")
            }
        }

    /**
     * Called when the activity is starting.
     * Sets up the ViewModels, location permission callback, and initiates the first weather fetch.
     * The city name is restored from shared preferences and weather data is fetched accordingly.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences =
            getSharedPreferences(getString(R.string.weatherappprefs), MODE_PRIVATE)
        val savedCity =
            sharedPreferences.getString(getString(R.string.city_name), getString(R.string.tokyo))
                ?: getString(R.string.tokyo)

        weatherViewModel.setRequestLocationPermissionLauncher(requestLocationPermissionLauncher)
        weatherViewModel.setupWeatherObserver(temperatureViewModel::insertTemperature)
        weatherViewModel.setCityName(savedCity)
        weatherViewModel.getLocalWeather(this)

        setContent {
            WeatherAppTheme {
                WeatherAppNavHost()
            }
        }
    }

    /**
     * Called when the activity is paused.
     * Saves the current city name in shared preferences to persist user preferences across sessions.
     */
    override fun onPause() {
        super.onPause()
        val sharedPreferences =
            getSharedPreferences(getString(R.string.weatherappprefs), MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(getString(R.string.city_name), weatherViewModel.getCityName().value)
            apply()
        }
    }
}