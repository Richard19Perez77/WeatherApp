package com.rperez.weatherapp.network.model

import com.rperez.weatherapp.repository.WeatherException

/**
 * State of calls to be reflected in UI
 */
sealed class WeatherState {
    data class CitySuccess(val data: WeatherResponse?) : WeatherState()
    data class LocalSuccess(val data: WeatherResponse?) : WeatherState()
    data class Failure(val data: WeatherException?) : WeatherState()
    object Loading : WeatherState()
}