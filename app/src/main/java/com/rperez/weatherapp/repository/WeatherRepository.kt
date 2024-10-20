package com.rperez.weatherapp.repository

import com.rperez.weatherapp.network.model.WeatherResponse


/**
 * Generic Weather Repository
 */
interface WeatherRepository {
    suspend fun getWeatherByCityData(cityName: String): Result<WeatherResponse>
    suspend fun getWeatherGeoData(lat: Double, lon: Double): Result<WeatherResponse>
}