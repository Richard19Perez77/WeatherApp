package com.rperez.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.rperez.weatherapp.data.local.db.TemperatureEntity
import com.rperez.weatherapp.navigation.WeatherAppNavHost
import com.rperez.weatherapp.ui.theme.WeatherAppTheme
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherState.Success
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

/**
 * Future implementations: help people be aware of problems in weather's effect on them.
 *
 * 1. illness associated with current temperature
 * 2. body difficulties in changing temperature
 * 3. humidity
 * 4. time of year seasonal transition alerts
 * 5. age as a factor, previous health concern
 *
 * A. DB for daily temp storage, if going to a new local, check complications that could arise from moving too fast, like pores not sweating as you expect even though you can usually handle really hot weather.
 *
 */
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModel()
    private val temperatureViewModel: TemperatureViewModel by viewModel()

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission was granted, now fetch the local weather
                viewModel.getLocalWeather(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        val savedCity = sharedPreferences.getString("CITY_NAME", "Tokyo") ?: "Tokyo"
        viewModel.setCityName(savedCity)
        viewModel.getWeather(savedCity)
        viewModel.weatherState.observeForever { observer ->
            if (viewModel.weatherState.value is Success) {
                var successTemp =
                    (viewModel.weatherState.value as Success).data?.main?.temp?.toDouble()
                if (successTemp != null) {
                    var temperatureEntity = TemperatureEntity(
                        date = "${LocalDateTime.now()}",
                        temperature = successTemp,
                    )
                    temperatureViewModel.insertTemperature(temperatureEntity)
                }
            }
        }
        viewModel.setRequestLocationPermissionLauncher(requestLocationPermissionLauncher)

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                WeatherAppNavHost(
                    navController = navController,
                    viewModel = viewModel,
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("CITY_NAME", viewModel.getCityName().value)
            apply()
        }
    }
}