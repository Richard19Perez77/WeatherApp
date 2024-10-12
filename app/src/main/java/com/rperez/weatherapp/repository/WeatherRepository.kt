package com.rperez.weatherapp.repository

import com.rperez.weatherapp.model.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherData(cityName: String): Result<WeatherResponse>
}