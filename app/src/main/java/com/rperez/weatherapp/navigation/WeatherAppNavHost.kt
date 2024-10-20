package com.rperez.weatherapp.navigation

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rperez.weatherapp.ui.screen.HeartScreen
import com.rperez.weatherapp.ui.screen.TemperatureScreen
import com.rperez.weatherapp.ui.screen.WeatherScreen
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel

/**
 *  Holds NavHost and sends screens data from the view model
 */
@Composable
fun WeatherAppNavHost(
    savedCity: String,
    requestLocationPermissionLauncher: ActivityResultLauncher<String>,
    navController: NavHostController,
    weatherViewModel: WeatherViewModel,
    temperatureViewModel: TemperatureViewModel,
) {

    LaunchedEffect(Unit) {
        temperatureViewModel.deleteAllTemperatures()
        temperatureViewModel.insertMockTemperatures()

        weatherViewModel.setRequestLocationPermissionLauncher(requestLocationPermissionLauncher)
        weatherViewModel.setupWeatherObserver(temperatureViewModel)
        weatherViewModel.setCityName(savedCity)
        weatherViewModel.getWeather(savedCity)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Search.route
    ) {

        composable(Screen.Search.route) {
            Scaffold(
                floatingActionButton = {
                    Fab(navController)
                },
                content = { padding ->
                    var modifier = Modifier.padding(padding)
                    WeatherScreen(
                        modifier = modifier,
                        navController = navController,
                        setCityName = weatherViewModel::setCityName,
                        getWeather = weatherViewModel::getWeather,
                        getLocalWeather = weatherViewModel::getLocalWeather,
                        cityName = weatherViewModel.cityName,
                        weatherState = weatherViewModel.weatherState
                    )
                }
            )
        }

        composable(Screen.Temp.route) {
            Scaffold(
                floatingActionButton = {
                    Fab(navController)
                },
                content = { padding ->
                    var modifier = Modifier.padding(padding)
                    TemperatureScreen(
                        modifier = modifier,
                        weatherState = weatherViewModel.weatherState
                    )
                }
            )
        }

        composable(Screen.Heart.route) {
            Scaffold(
                content = { padding ->
                    var modifier = Modifier.padding(padding)
                    HeartScreen(
                        modifier = modifier,
                        getAllTemperatures = temperatureViewModel::getAllTemperatures
                    )
                }
            )
        }
    }
}

@Composable
private fun Fab(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            val currentBackStackEntry = navController.currentBackStackEntry
            if (currentBackStackEntry?.destination?.route != Screen.Heart.route) {
                navController.navigate(Screen.Heart.route) {
                    popUpTo(Screen.Heart.route) { inclusive = false }
                }
            }
        }
    ) {
        Icon(Icons.Filled.Favorite, contentDescription = "Heart")
    }
}