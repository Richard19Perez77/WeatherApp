package com.rperez.weatherapp.viewmodel

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rperez.weatherapp.model.WeatherResponse
import com.rperez.weatherapp.repository.WeatherException
import com.rperez.weatherapp.repository.WeatherRepository
import com.rperez.weatherapp.service.LocationService
import com.rperez.weatherapp.viewmodel.WeatherState.Failure
import com.rperez.weatherapp.viewmodel.WeatherState.Success
import kotlinx.coroutines.launch

/**
 * View model to hold the Weather State as calls change it.
 */
class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    lateinit var launcher: ActivityResultLauncher<String>

    private val _cityName = mutableStateOf("")
    val cityName = _cityName

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    lateinit var coords: Pair<Double, Double>

    fun getCityName(): State<String> {
        return cityName
    }

    fun setCityName(cityName: String) {
        _cityName.value = cityName
    }

    fun getWeather(cityName: String) {
        if (cityName.isNotEmpty()) {
            viewModelScope.launch {
                _weatherState.value = WeatherState.Loading
                val result = repository.getWeatherByCityData(cityName)
                result.onSuccess {
                    _weatherState.value = Success(result.getOrNull())
                    _cityName.value = (_weatherState.value as Success).data?.name ?: ""
                }
                result.onFailure {
                    _weatherState.value = Failure(result.exceptionOrNull() as WeatherException?)
                }
            }
        }
    }


    /**
     * Use Location to get local weather, not by city
     */
    fun getLocalWeather(context: Context) {
        if (::coords.isInitialized) {
            viewModelScope.launch {
                _weatherState.value = WeatherState.Loading
                val result = repository.getWeatherGeoData(coords.first, coords.second)
                result.onSuccess {
                    _weatherState.value = Success(result.getOrNull())
                    _cityName.value = (_weatherState.value as Success).data?.name ?: ""
                }
                result.onFailure {
                    _weatherState.value = Failure(result.exceptionOrNull() as WeatherException?)
                }
            }
        } else {
            LocationService(context, launcher).getLatLon(
                onLocationReceived = { lat, lon ->
                    coords = Pair(lat, lon)
                    getLocalWeather(context)
                },
                onPermissionRequired = {
                    AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the location permission to fetch weather data for your current location.")
                        .setPositiveButton("Grant") { _, _ ->
                            launcher.launch(ACCESS_FINE_LOCATION)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            )
        }
    }

    fun setRequestLocationPermissionLauncher(launcher: ActivityResultLauncher<String>) {
        this.launcher = launcher
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
