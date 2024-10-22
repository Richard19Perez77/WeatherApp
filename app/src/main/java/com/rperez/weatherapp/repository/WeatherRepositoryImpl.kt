package com.rperez.weatherapp.repository

import com.rperez.weatherapp.BuildConfig
import com.rperez.weatherapp.network.ApiClient
import com.rperez.weatherapp.network.WeatherService
import com.rperez.weatherapp.network.model.WeatherResponse
import retrofit2.Call
import retrofit2.HttpException

/**
 * Throw in case of Failure, will need to add more testing for fail states.
 */
class WeatherException(message: String) : Exception(message)

/**
 * Performs work to get weather data, holds API Key and may throw a WeatherException
 */
class WeatherRepositoryImpl : WeatherRepository {

    var apiKey = BuildConfig.API_KEY

    private val weatherService: WeatherService =
        ApiClient.retrofit.create(WeatherService::class.java)

    /**
     * Call for data by city from text field.
     */
    override suspend fun getWeatherByCityData(
        cityName: String
    ): Result<WeatherResponse> {
        return try {
            val response = weatherService.getWeather(cityName, apiKey)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(WeatherException("HTTP Error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(WeatherException("Network Error: ${e.message}"))
        }
    }

    /**
     * Get weather by lat and long coords, will need permissions granted for this to work.
     */
    override suspend fun getWeatherGeoData(
        lat: Double,
        lon: Double
    ): Result<WeatherResponse> {
        return try {
            val response = weatherService.getGeoCoordWeather(lat, lon, apiKey)
            Result.success(response)
        } catch (e: HttpException) {
            Result.failure(WeatherException("HTTP Error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(WeatherException("Network Error: ${e.message}"))
        }
    }
}