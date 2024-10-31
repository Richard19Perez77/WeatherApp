package com.rperez.weatherapp.repository

import android.content.Context
import com.rperez.weatherapp.BuildConfig
import com.rperez.weatherapp.network.ApiClient
import com.rperez.weatherapp.network.WeatherService
import com.rperez.weatherapp.network.model.WeatherResponse
import retrofit2.HttpException

/**
 * Custom exception to indicate failures in weather data retrieval.
 *
 * This exception should be thrown when an error occurs during the API calls,
 * allowing for better handling of error states.
 */
class WeatherException(message: String) : Exception(message)

/**
 * Implementation of the WeatherRepository interface that handles data retrieval
 * from the weather service API. Holds the API key required for requests and
 * can throw WeatherException in case of failures.
 */
class WeatherRepositoryImpl : WeatherRepository {

    private val weatherService: WeatherService =
        ApiClient.retrofit.create(WeatherService::class.java)

    /**
     * Retrieves weather data for a specified city.
     *
     * Makes a network call to the weather service using the provided city name.
     *
     * @param cityName The name of the city for which to retrieve weather data.
     * @return A Result containing either the WeatherResponse object on success
     *         or a WeatherException on failure.
     */
    override suspend fun getWeatherByCityData(
        context: Context,
        apiKey : (Context) -> String,
        cityName: String
    ): Result<WeatherResponse> {
        return try {
            val response = weatherService.getWeather(cityName, apiKey.invoke(context))
            Result.success(response)
        } catch (e: HttpException) {
            var code: Int = e.code()
            when (code) {
                404 -> {
                    Result.failure(WeatherException("No data for city"))
                }

                else -> Result.failure(WeatherException("Service Error"))
            }
        } catch (e: Exception) {
            Result.failure(WeatherException("Unknown Error."))
        }
    }

    /**
     * Retrieves weather data based on geographical coordinates (latitude and longitude).
     *
     * This method requires appropriate permissions to access location data.
     *
     * @param lat The latitude of the desired location.
     * @param lon The longitude of the desired location.
     * @return A Result containing either the WeatherResponse object on success
     *         or a WeatherException on failure.
     */
    override suspend fun getWeatherGeoData(
        context: Context,
        apiKey : (Context) -> String,
        lat: Double,
        lon: Double
    ): Result<WeatherResponse> {
        return try {
            val response = weatherService.getGeoCoordWeather(lat, lon, apiKey.invoke(context))
            Result.success(response)
        } catch (e: HttpException) {
            var code: Int = e.code()
            when (code) {
                404 -> {
                    Result.failure(WeatherException("No data for your location"))
                }

                else -> Result.failure(WeatherException("Service Error"))
            }
        } catch (_: Exception) {
            Result.failure(WeatherException("Unknown Error"))
        }
    }
}