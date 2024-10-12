package com.rperez.weatherapp.repository

import com.rperez.weatherapp.model.WeatherResponse

/**
 * Generic Weather Repository
 */
interface WeatherRepository {
    suspend fun getWeatherData(cityName: String): Result<WeatherResponse>
}