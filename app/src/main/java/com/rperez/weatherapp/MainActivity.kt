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
import com.rperez.weatherapp.viewmodel.WeatherState
import com.rperez.weatherapp.viewmodel.WeatherState.CitySuccess
import com.rperez.weatherapp.viewmodel.WeatherState.LocalSuccess
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.util.Locale
import kotlin.text.uppercase

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
        temperatureViewModel.insertMockTemperatures()
        weatherViewModel.setCityName(savedCity)
        weatherViewModel.getWeather(savedCity)
        weatherViewModel.weatherState.observeForever { observer ->
            var data = "${LocalDate.now()}"
            when (weatherViewModel.weatherState.value) {
                is CitySuccess -> {
                    var item = (weatherViewModel.weatherState.value as CitySuccess)
                    item.data?.main?.temp?.toDouble().let {
                        if (it != null) {
                            var temperatureEntity = TemperatureEntity(
                                date = data,
                                temperature = it,
                                local = false,
                                city = item.data?.name ?: "",
                                desc = item.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                                    it.uppercase(Locale.ROOT)
                                }.toString(),
                                humidity = item.data?.main?.humidity ?: Int.MIN_VALUE,
                                pressure = item.data?.main?.pressure ?: Int.MIN_VALUE,
                            )
                            temperatureViewModel.insertTemperature(temperatureEntity)
                        }
                    }
                }

                is LocalSuccess -> {
                    var item = (weatherViewModel.weatherState.value as LocalSuccess)
                    item.data?.main?.temp?.toDouble().let {
                        if (it != null) {
                            var temperatureEntity = TemperatureEntity(
                                date = data,
                                temperature = it,
                                local = true,
                                city = item.data?.name ?: "",
                                desc = item.data?.weather?.firstOrNull()?.description?.replaceFirstChar {
                                    it.uppercase(Locale.ROOT)
                                }.toString(),
                                humidity = item.data?.main?.humidity ?: Int.MIN_VALUE,
                                pressure = item.data?.main?.pressure ?: Int.MIN_VALUE,
                            )
                            temperatureViewModel.insertTemperature(temperatureEntity)
                        }
                    }
                }

                is WeatherState.Failure -> {}
                is WeatherState.Loading -> {}
                null -> {}
            }
        }
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