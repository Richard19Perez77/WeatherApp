package com.rperez.weatherapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.viewmodel.WeatherState.Success
import com.rperez.weatherapp.viewmodel.WeatherState.Failure
import kotlinx.coroutines.launch

/**
 * View model to hold the Weather State as calls change it.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    fun getCityName(): State<String> {
        return cityName
    }

    fun setCityName(cityName: String) {
        _cityName.value = cityName
    }

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

/**
 * State of calls to be reflected in UI
 */
sealed class WeatherState {
    data class Success(val data: WeatherResponse?) : WeatherState()
    data class Failure(val data: WeatherException?) : WeatherState()
    object Loading : WeatherState()
}
