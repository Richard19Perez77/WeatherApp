package com.rperez.weatherapp.navigation

/**
 * Screens for UI for user to navigate
 */
sealed class Screen(val route: String) {
    object Search : Screen("Search")
    object Temp : Screen("Temp")
    object Heart : Screen("Heart")
}