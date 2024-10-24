package com.rperez.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object that provides the Retrofit client for making API calls.
 *
 * This object is responsible for configuring the Retrofit instance,
 * which is used to interact with the OpenWeatherMap API. It uses GSON
 * as the converter factory to automatically parse JSON responses.
 *
 * @property BASE_URL The base URL for the OpenWeatherMap API.
 * @property retrofit The configured Retrofit instance, initialized lazily.
 */
object ApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
