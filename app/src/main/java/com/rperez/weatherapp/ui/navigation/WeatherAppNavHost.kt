package com.rperez.weatherapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rperez.weatherapp.viewmodel.TemperatureViewModel
import com.rperez.weatherapp.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Sets up the navigation host for the Weather application.
 * This function defines the navigation graph and manages transitions between screens.
 * It utilizes the ViewModel to handle data related to weather and temperature.
 *
 * The main screens included are:
 * - Search Screen: For searching weather information.
 * - Temperature Screen: Displays temperature-related data.
 * - Heart Screen: Shows favorite temperatures.
 */
@Composable
fun WeatherAppNavHost() {
    val navController: NavHostController = rememberNavController()

    // Obtain instances of the WeatherViewModel and TemperatureViewModel using Koin
    val weatherViewModel: WeatherViewModel = koinViewModel()
    val temperatureViewModel: TemperatureViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Search.route
    ) {
        // Define the Search screen
        composable(Screen.Search.route) {
            Scaffold(
                floatingActionButton = {
                    Fab(navController) // Floating action button for navigation
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

        // Define the Temperature screen
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

        // Define the Heart screen for health according to temperatures
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

/**
 * Floating Action Button (FAB) used for navigating to the Heart screen.
 * It checks the current back stack and navigates to the Heart screen if it's not already displayed.
 */
@Composable
private fun Fab(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            val currentBackStackEntry = navController.currentBackStackEntry

            // Navigate to Heart screen if not already there
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