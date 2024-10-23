package com.rperez.weatherapp.network.model

/**
 * Data class to represent UI state variables
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