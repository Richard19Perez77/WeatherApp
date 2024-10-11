package com.rperez.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.model.Main
import com.rperez.weatherapp.model.Weather
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    fun getWeather(cityName: String, apiKey: String) {
        viewModelScope.launch {
            val response = repository.getWeatherData(cityName, apiKey)
            response?.let {
                _weatherData.postValue(it)
            } ?: run {
                // Handle error case
                _weatherData.postValue(
                    WeatherResponse(
                        Main(0.0, 0),
                        listOf(Weather("Error fetching data", ""))
                    )
                )
            }
        }
    }
}

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
//
//    private val _weatherData = MutableLiveData<WeatherResponse>()
//    val weatherData: LiveData<WeatherResponse> get() = _weatherData
//
//    fun getWeather(cityName: String, apiKey: String) {
//        viewModelScope.launch {
//            val response = repository.getWeatherData(cityName, apiKey)
//            _weatherData.postValue(response.value)
//        }
//
//        viewModelScope.launch {
//            try {
//                val response = repository.getWeatherData(cityName, apiKey)
//                if (response.isSuccessful) {
//                    _weatherData.postValue(response.body())
//                    Log.d("WeatherViewModel", "Weather data: ${response.body()}")
//                } else {
//                    Log.e("WeatherViewModel", "API call failed: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("WeatherViewModel", "Exception: ${e.message}")
//            }
//        }
//
//    }
//}

//class WeatherViewModelFactory(private val repository: WeatherRepository) :
//    ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
//            return WeatherViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

