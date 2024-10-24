package com.rperez.weatherapp.network.model

/**
 * Data class that represents the response from the weather API.
 *
 * @property main Contains the main weather details such as temperature, humidity, and pressure.
 * @property weather List of weather conditions, each describing the current state of the weather.
 * @property name The name of the location (city) for which the weather is provided.
 */
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

/**
 * Data class representing the main weather metrics.
 *
 * @property temp The current temperature in Celsius.
 * @property humidity The percentage of humidity in the air.
 * @property pressure The atmospheric pressure in hPa (hectopascals).
 */
data class Main(
    val temp: Double,
    val humidity: Int,
    val pressure: Int,
)

/**
 * Data class representing the weather conditions.
 *
 * @property description A brief description of the weather (e.g., "clear sky").
 * @property icon A string representing the icon for the current weather conditions,
 *                which can be used to display an appropriate weather image.
 */
data class Weather(
    val description: String,  // Weather condition description (e.g., "clear sky")
    val icon: String          // Icon for weather icon URL
)