package com.rperez.weatherapp.ui.navigation

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
import androidx.navigation.compose.rememberNavController
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

/**
 *  Holds NavHost and sends screens data from the view model
 */
@Composable
fun WeatherAppNavHost(
    savedCity: String,
    requestLocationPermissionLauncher: ActivityResultLauncher<String>,
) {
    val navController: NavHostController = rememberNavController()
    val weatherViewModel: WeatherViewModel = koinViewModel()
    val temperatureViewModel: TemperatureViewModel = koinViewModel()

    /**
     * Start of work for demo, using mock data and observers as well as initial setup for weather from city of last use.
     */
    LaunchedEffect(Unit) {
        temperatureViewModel.insertMockTemperatures()
        weatherViewModel.setRequestLocationPermissionLauncher(requestLocationPermissionLauncher)
        weatherViewModel.setupWeatherObserver(temperatureViewModel::insertTemperature)
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
                        weatherUIState = weatherViewModel.uiState,
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
                        weatherUIState = weatherViewModel.uiState,
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