package com.rperez.weatherapp.repository

import retrofit2.HttpException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rperez.weatherapp.model.Main
import com.rperez.weatherapp.model.Weather
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.network.ApiClient
import com.rperez.weatherapp.network.WeatherService

class WeatherRepository {

    private val weatherService: WeatherService =
        ApiClient.getClient().create(WeatherService::class.java)

    suspend fun getWeatherData(cityName: String, apiKey: String): WeatherResponse? {
        return try {
            weatherService.getWeather(cityName, apiKey)
        } catch (e: HttpException) {
            WeatherResponse(
                Main(temp = 0.0, humidity = 0),
                listOf(Weather(description = "${e.message}", icon = ""))
            )
        } catch (e: Exception) {
            WeatherResponse(
                Main(temp = 0.0, humidity = 0),
                listOf(Weather(description = "${e.message}", icon = ""))
            )
        }
    }

//    suspend fun getWeatherData(cityName: String, apiKey: String): LiveData<WeatherResponse> {
//        val data = MutableLiveData<WeatherResponse>()
//
//        try {
//            val response = weatherService.getWeather(cityName, apiKey)
//            data.postValue(response) // Post the result on success
//        } catch (e: HttpException) {
//            data.postValue(WeatherResponse(
//                Main(temp = 0.0, humidity =  0),
//                listOf(Weather(description = "${e.message}", icon = ""))
//            ))
//        } catch (e: Exception) {
//            data.postValue(WeatherResponse(Main(temp = 0.0, humidity =  0), listOf(Weather(description = "${e.message}", icon = ""))))
//        }
//
//        return data
//    }
}
