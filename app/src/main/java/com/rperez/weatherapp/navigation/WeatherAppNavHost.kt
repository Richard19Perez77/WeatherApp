package com.rperez.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rperez.weatherapp.ui.screen.TemperatureScreen
import com.rperez.weatherapp.ui.screen.WeatherScreen
import com.rperez.weatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherAppNavHost(
    navController: NavHostController,
    viewModel: WeatherViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            WeatherScreen(getWeather = viewModel::getWeather, viewModel.weatherState)
        }
        composable(Screen.Temp.route) {
            TemperatureScreen(weatherState = viewModel.weatherState.value)
        }
    }
}