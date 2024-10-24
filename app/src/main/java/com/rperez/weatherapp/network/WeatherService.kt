package com.rperez.weatherapp.network

import com.rperez.weatherapp.network.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for Weather API service using Retrofit.
 *
 * Defines the endpoints for retrieving weather data either by city name or
 * geographical coordinates. The results are automatically converted to
 * WeatherResponse objects.
 */
interface WeatherService {

    /**
     * Fetches weather data by city name.
     *
     * @param cityName The name of the city for which to fetch weather data.
     * @param apiKey The API key to authenticate the request.
     * @param units The unit of temperature (e.g., metric, imperial). Default is "metric".
     * @return The weather data in the form of a WeatherResponse object.
     */
    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    /**
     * Fetches weather data by geographical coordinates (latitude and longitude).
     *
     * @param lat The latitude of the location.
     * @param long The longitude of the location.
     * @param apiKey The API key to authenticate the request.
     * @param units The unit of temperature (e.g., metric, imperial). Default is "metric".
     * @return The weather data in the form of a WeatherResponse object.
     */
    @GET("weather")
    suspend fun getGeoCoordWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
