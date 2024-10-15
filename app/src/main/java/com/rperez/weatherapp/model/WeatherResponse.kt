package com.rperez.weatherapp.model

/**
 * Map the response to a weather data class
 */
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

/**
 * Daily vars for weather
 */
data class Main(
    val temp: Double,   // Temperature
    val humidity: Int   // Humidity
)

/**
 * Weather conditions
 */
data class Weather(
    val description: String,  // Weather condition description (e.g., "clear sky")
    val icon: String          // Icon for weather condition
)