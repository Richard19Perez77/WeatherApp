package com.rperez.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.viewmodel.WeatherState.Success
import com.rperez.weatherapp.viewmodel.WeatherState.Failure
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    fun getWeather(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            val result = repository.getWeatherData(cityName)
            result.onSuccess {
                _weatherState.value = Success(result.getOrNull())
            }
            result.onFailure {
                _weatherState.value = Failure(result.exceptionOrNull() as WeatherException?)
            }
        }
    }
}

sealed class WeatherState {
    data class Success(val data: WeatherResponse?) : WeatherState()
    data class Failure(val data: WeatherException?) : WeatherState()
    object Loading : WeatherState()
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