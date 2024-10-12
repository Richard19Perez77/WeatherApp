package com.rperez.weatherapp.repository

import com.rperez.weatherapp.BuildConfig
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.network.ApiClient
import com.rperez.weatherapp.network.WeatherService
import retrofit2.HttpException

class WeatherException(message: String) : Exception(message)

class WeatherRepositoryImpl : WeatherRepository {

    var apiKey = BuildConfig.API_KEY

    private val weatherService: WeatherService =
        ApiClient.getClient().create(WeatherService::class.java)

    override suspend fun getWeatherData(cityName: String): Result<WeatherResponse> {
        return try {
            val response = weatherService.getWeather(cityName, apiKey)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(WeatherException("HTTP Error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(WeatherException("Network Error: ${e.message}"))
        }
    }
}