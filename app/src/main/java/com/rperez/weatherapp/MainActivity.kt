package com.rperez.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.rperez.weatherapp.navigation.WeatherAppNavHost
import com.rperez.weatherapp.ui.theme.WeatherAppTheme
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Future implementations: help people be aware of problems in weather's effect on them.
 *
 * may need to alter the air pressure by city zip code for normal values or expected unhealthy values
 * what temperatures are most important in list, newest first
 * trend graphs at all or a simple line graph per city, based on previous day small line trend with markers for temps
 * most likely move the view model int the composables and nav controller
 * defensive programming for api client successive calls
 *  detect small trends of temperature from default city to loca (city uses default city coords, local is variable by a small amount, user won't know city coords)
 *  remove fab for heart screen
 *
 * /
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModel()
    private val temperatureViewModel: TemperatureViewModel by viewModel()

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                weatherViewModel.getLocalWeather(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        val savedCity = sharedPreferences.getString("CITY_NAME", "Tokyo") ?: "Tokyo"

        temperatureViewModel.deleteAllTemperatures()
        temperatureViewModel.insertMockTemperatures()
        weatherViewModel.setCityName(savedCity)
        weatherViewModel.getWeather(savedCity)
        weatherViewModel.setupWeatherObserver(temperatureViewModel)
        weatherViewModel.setRequestLocationPermissionLauncher(requestLocationPermissionLauncher)

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                WeatherAppNavHost(
                    navController = navController,
                    weatherViewModel = weatherViewModel,
                    temperatureViewModel = temperatureViewModel,
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("CITY_NAME", weatherViewModel.getCityName().value)
            apply()
        }
    }
}