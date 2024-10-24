package com.rperez.weatherapp.network.model

/**
 * Data class representing the UI state for weather information.
 *
 * @property isLoading Indicates whether the weather data is currently being loaded.
 * @property temperature The current temperature to be displayed, or a default value if unavailable.
 * @property humidity The percentage of humidity to be displayed, or a default value if unavailable.
 * @property airPressure The current atmospheric pressure in hPa to be displayed, or a default value if unavailable.
 * @property name The name of the location (e.g., city) to be displayed.
 * @property description A brief description of the current weather conditions (e.g., "clear sky").
 * @property icon A string representing the icon used for the current weather conditions.
 * @property errorMessage A string containing any error message to be displayed if an error occurs while fetching data.
 */
data class WeatherUI(
    val isLoading: Boolean = true,
    val temperature: Double = Double.MIN_VALUE,
    val humidity: Int = Int.MIN_VALUE,
    val airPressure: Int = Int.MIN_VALUE,
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val errorMessage: String = ""
)