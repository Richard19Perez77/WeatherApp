package com.rperez.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rperez.weatherapp.ui.screen.TemperatureScreen
import com.rperez.weatherapp.ui.screen.WeatherScreen
import com.rperez.weatherapp.viewmodel.WeatherViewModel

/**
 *  Holds NavHost and sends screens data from the view model
 */
@Composable
fun WeatherAppNavHost(
    navController: NavHostController,
    viewModel: WeatherViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            WeatherScreen(
                navController = navController,
                setCityName = viewModel::setCityName,
                getWeather = viewModel::getWeather,
                cityName = viewModel.cityName,
                weatherState = viewModel.weatherState
            )
        }
        composable(Screen.Temp.route) {
            TemperatureScreen(weatherState = viewModel.weatherState)
        }
    }
}