package com.rperez.weatherapp.navigation

sealed class Screen(val route: String) {
    object Search : Screen("Search")
    object Temp : Screen("Temp")
}