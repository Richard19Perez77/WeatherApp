package com.rperez.weatherapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.rperez.weatherapp.navigation.WeatherAppNavHost
import com.rperez.weatherapp.ui.theme.WeatherAppTheme
import com.rperez.weatherapp.viewmodel.LOCATION_PERMISSION_REQUEST_CODE
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * MainActivity starting point of application.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        val savedCity = sharedPreferences.getString("CITY_NAME", "Tokyo") ?: "Tokyo"
        viewModel.setCityName(savedCity)
        viewModel.getWeather(savedCity)

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, now fetch the local weather
                    viewModel.getLocalWeather(this)
                }
            }
        }
    }
}