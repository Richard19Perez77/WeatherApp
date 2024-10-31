package com.rperez.weatherapp.repository

import android.content.Context
import com.rperez.weatherapp.network.model.WeatherResponse


/**
 * Interface representing a generic Weather Repository.
 *
 * Defines methods to fetch weather data either by city name or geographical coordinates.
 * The repository handles data retrieval and can provide results in a standardized format.
 */
interface WeatherRepository {

    /**
     * Retrieves weather data for a specified city.
     *
     * @param cityName The name of the city for which to fetch weather data.
     * @return A Result containing the WeatherResponse object or an error if the request fails.
     */
    suspend fun getWeatherByCityData(context: Context,
                                     apiKey : (Context) -> String,
                                     cityName: String): Result<WeatherResponse>

    /**
     * Retrieves weather data for a specified geographical location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A Result containing the WeatherResponse object or an error if the request fails.
     */
    suspend fun getWeatherGeoData(context: Context,
                                  apiKey : (Context) -> String,
                                  lat: Double, lon: Double): Result<WeatherResponse>
}