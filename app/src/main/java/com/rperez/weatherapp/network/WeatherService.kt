package com.rperez.weatherapp.network

import com.rperez.weatherapp.network.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Weather Service API
 */
interface WeatherService {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("weather")
    suspend fun getGeoCoordWeather(
        @Query("lat") lat: Double,
        @Query("lon") long: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
