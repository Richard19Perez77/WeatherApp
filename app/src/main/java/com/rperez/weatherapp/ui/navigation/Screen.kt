package com.rperez.weatherapp.ui.navigation

/**
 * Represents the different screens available in the app's navigation system.
 * Each screen is defined with a unique [route] string, which is used for navigation.
 *
 * This sealed class ensures that only the specified screens can be used, offering
 * type safety when navigating between different UI components.
 *
 * @property route A unique string identifier for the screen used in the navigation.
 */
sealed class Screen(val route: String) {

    // Represents the search screen where users can search for weather-related information.
    object Search : Screen("Search")

    // Represents the temperature screen, which displays the current temperature.
    object Temp : Screen("Temp")

    // Represents the heart screen, which displays unique temperature items saved in Room and/or health tips card.
    object Heart : Screen("Heart")
}