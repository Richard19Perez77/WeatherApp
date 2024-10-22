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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rperez.weatherapp.ui.screen.HeartScreen
import com.rperez.weatherapp.ui.screen.TemperatureScreen
import com.rperez.weatherapp.ui.screen.WeatherScreen
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

/**
 *  Holds NavHost and sends screens data from the view model
 */
@Composable
fun WeatherAppNavHost(
    weatherViewModel: WeatherViewModel = koinViewModel(),
    temperatureViewModel: TemperatureViewModel = koinViewModel(),
    savedCity: String,
    requestLocationPermissionLauncher: ActivityResultLauncher<String>,
    navController: NavHostController = rememberNavController(),
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        temperatureViewModel.insertMockTemperatures()
        weatherViewModel.setRequestLocationPermissionLauncher(requestLocationPermissionLauncher)
        weatherViewModel.setupWeatherObserver(temperatureViewModel::insertTemperature, lifecycleOwner = lifecycleOwner)
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